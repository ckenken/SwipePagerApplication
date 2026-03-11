# SwipePageApplication

一個 Jetpack Compose 的 2D 空間導航 Demo，透過上下左右滑動在頁面間切換，支援 Deep Link 直接跳轉。

## 頁面空間配置

```
           [B] Profile
               ↑
[E] Chat ← [Home] → [F] Stats
               ↓
           [C] Feed
               ↓
           [D] Detail
```

- Home 可往四個方向滑動
- C ↔ D 垂直相連
- B、E、F 各只有一個方向可回到 Home

---

## 1. 頁面滑動如何實現

本專案**沒有使用** `ViewPager`、`HorizontalPager`、或 Navigation Compose 的 `NavHost`，而是自建了一個 2D progressive pager（`SpatialPager.kt`）。

### 空間圖（Spatial Graph）

頁面間的鄰居關係定義在 `SpatialNavigation.kt` 的 `spatialGraph` 中：

```kotlin
val spatialGraph: Map<Page, PageNeighbors> = mapOf(
    Page.Home to PageNeighbors(up = Page.B, down = Page.C, left = Page.E, right = Page.F),
    Page.B    to PageNeighbors(down = Page.Home),
    Page.C    to PageNeighbors(up = Page.Home, down = Page.D),
    // ...
)
```

這是一個**非對稱的有向圖**，不是規則的 grid。新增頁面只需在此 map 加一筆。

### 手勢處理

`SpatialPager` 使用 `Modifier.pointerInput` + `detectDragGestures` 追蹤手指拖曳：

1. **方向鎖定**：首次拖曳確定水平或垂直後，鎖定該軸（避免斜向滑動）
2. **邊界限制**：如果某方向沒有鄰居（例如在 Page B 往上滑），`offset` 被 clamp 在 0，手指拖不動
3. **跟手滑動**：拖曳過程中，當前頁和目標頁透過 `graphicsLayer { translationX/Y }` 同步位移，兩頁緊鄰排列，視覺上像一張連續的紙被拖動

### 放手後的動畫

放手時根據拖曳距離決定行為：

- **超過螢幕 30%** → spring 動畫滑到底，完成換頁（`onPageChanged` callback）
- **未超過 30%** → spring 動畫彈回原位

```kotlin
val snap = abs(offsetX) > screenWidth * 0.3f && target != null
val end = if (snap) screenWidth else 0f
animate(offsetX, end, animationSpec = spring(stiffness = 500f)) { v, _ ->
    offsetX = v
}
```

### 返回鍵

`SpatialNavHost` 維護一個手動的 `backStack: SnapshotStateList<Page>`。每次換頁時將當前頁 push 進 stack，`BackHandler` 在按返回鍵時 pop 回上一頁。

---

## 2. Deep Link 如何動作

### URI 格式

```
swipepage://app/{route}
```

例如 `swipepage://app/d` 會直接開啟 Detail 頁面。

### 運作流程

**App 未啟動時（冷啟動）：**

1. 系統透過 `AndroidManifest.xml` 的 intent-filter 啟動 `MainActivity`
2. `MainActivity.onCreate` 從 `intent.data` 解析出目標頁面
3. 將解析結果作為 `initialPage` 傳入 `SpatialNavHost`
4. Pager 直接以該頁為起始頁渲染

**App 已在前景（singleTop）：**

1. `AndroidManifest.xml` 設定 `android:launchMode="singleTop"`，不會建立新 Activity
2. `onNewIntent` 收到新 intent，解析後寫入 `deepLinkTarget` state
3. `SpatialNavHost` 中的 `LaunchedEffect(externalNavigation.value)` 偵測到變化
4. 將當前頁推入 back stack，切換到目標頁

### Deep Link 後空間關係不變

Deep link 只改變「你在哪一頁」，不影響 `spatialGraph`。所以 deep link 到 Page D 後，往上滑仍然會到 Page C，再往上到 Home — 空間關係完全保留。

### 測試指令

```bash
adb shell am start -a android.intent.action.VIEW -d "swipepage://app/home"
adb shell am start -a android.intent.action.VIEW -d "swipepage://app/b"
adb shell am start -a android.intent.action.VIEW -d "swipepage://app/c"
adb shell am start -a android.intent.action.VIEW -d "swipepage://app/d"
adb shell am start -a android.intent.action.VIEW -d "swipepage://app/e"
adb shell am start -a android.intent.action.VIEW -d "swipepage://app/f"
```

---

## 3. 為什麼每次只 load 一頁（最多兩頁）

Compose 是宣告式 UI — **composable function 沒被呼叫就不存在於 composition tree 中**。

### 靜止時：1 頁

```kotlin
// targetPage 在 offset 為 0 時是 null
val targetPage = when {
    offsetX > 0 -> nbrs?.left
    offsetX < 0 -> nbrs?.right
    // ...
    else -> null  // ← 靜止時走這裡
}

// null → let block 不執行 → pageContent(target) 不會被呼叫
targetPage?.let { target ->
    Box(...) { pageContent(target) }
}

// 只有這一個 pageContent 被呼叫
Box(...) { pageContent(currentPage) }
```

而 `pageContent` 內部是一個 `when` 分支：

```kotlin
when (page) {
    Page.Home -> HomeScreen()   // ← 只有命中的分支會執行
    Page.B -> ProfileScreen()   // ← 其他完全不會被呼叫
    // ...
}
```

### 拖曳時：2 頁

手指開始拖曳 → `offsetX != 0` → `targetPage` 變成非 null → `pageContent(target)` 被呼叫 → 目標頁被 compose。

### 放手後：回到 1 頁

換頁完成後 offset 歸零，舊頁面的 composable 從 tree 中移除，內部的 `remember` 狀態隨之銷毀。

### 與 XML View 的差異

| | Compose | XML View |
|---|---|---|
| 不顯示的頁面 | 不存在（函式未被呼叫） | 存在但 `GONE`（仍佔記憶體） |
| 頁面切換 | 動態進出 composition tree | 切換 visibility |
| 狀態保留 | 離開 composition 後丟失 | View 仍在，狀態保留 |

---

## 4. 其他設計說明

### 為什麼不用 Navigation Compose 的 NavHost？

NavHost 的頁面切換是瞬間完成的（搭配 enter/exit transition 動畫），無法做到「手指拖到一半看到下一頁」的 progressive 效果。為了實現跟手滑動，必須自建 pager 來同時渲染兩個頁面並控制它們的位移。

### 為什麼不用 HorizontalPager / VerticalPager？

這些元件只支援單一軸向。本專案需要 2D 導航（上下左右都能滑），且頁面關係不是規則的 grid（例如 D 只能到 C，不能直接到 Home），用巢狀 pager 處理非對稱關係會非常痛苦。

### 已知限制

- **頁面狀態不保留**：離開一個頁面後再滑回來，頁面內部的 `remember` 狀態會重置（例如 Chat 頁面打到一半的文字、Profile 的 toggle 狀態）。如果需要保留，可以將狀態提升到 ViewModel 或用 `rememberSaveable`。
- **拖曳首幀可能掉幀**：目標頁在拖曳開始時才進行首次 compose，如果頁面非常重（大量圖片、複雜佈局），第一幀可能有短暫 jank。
- **拖曳與子元件手勢衝突**：頁面內的可滾動元件（LazyColumn）或可拖曳元件（Slider）會與 pager 的手勢偵測衝突。目前的頁面設計刻意避免使用滾動佈局。

### 專案結構

```
app/src/main/java/com/example/swipepageapplication/
├── MainActivity.kt                  # Activity，處理 deep link
├── navigation/
│   ├── SpatialNavigation.kt         # Page enum、空間圖定義
│   ├── SpatialPager.kt              # 核心 2D progressive pager
│   └── SpatialNavHost.kt            # 頁面對應、back stack、導航提示
├── screens/
│   ├── HomeScreen.kt                # Dashboard
│   ├── ProfileScreen.kt             # Page B - 個人資料
│   ├── FeedScreen.kt                # Page C - 探索
│   ├── DetailScreen.kt              # Page D - 內容詳情
│   ├── ChatScreen.kt                # Page E - 聊天
│   └── StatsScreen.kt               # Page F - 數據分析
└── ui/theme/                         # Material3 主題
```
