# MultiStatusView

一个轻量级、可灵活扩展的Android多状态视图管理库，支持多种配置模式，轻松实现内容加载、空数据、错误等状态切换。

## 功能特点

- **无限状态**：无预定义状态，开发者可以根据需要自定义任何状态
- **三种配置模式**：XML约定名称、资源ID、布局资源，适应不同开发场景
- **简单自定义容器**：采用MultiStatusHelper + 委托模式，核心逻辑与容器解耦
- **高度可扩展**：可基于任意ViewGroup快速实现自定义MultiStatusView
- **零依赖**：仅依赖Android SDK，无第三方库依赖
- **易集成**：支持现有项目无缝集成，最小化代码改动
- **错误处理**：支持状态未找到、视图异常等情况的处理
- **泛型支持**：StatusViewManager 和 StateManager 支持泛型，可使用任意类型的状态

![example](./example1.jpg)

## 快速开始

### 1. 添加依赖

在您的 `build.gradle` 文件中添加：

```gradle
implementation 'io.github.xesam:android-multistatusview:0.0.3'
```

或者直接引入项目：

```gradle
dependencies {
    implementation project(':MultiStatusView')
}
```

### 2. 基本使用

#### XML约定名称（推荐）

通过 `app:statusIdPrefix` 前缀和状态名称约定，MultiStatusView 会自动查找对应的视图ID。
例如，状态名称为 `content`，则对应的视图ID应为 `@+id/status_content`。

组件默认的前缀为 `status_`，可以通过 `app:statusIdPrefix` 自定义，注意不要遗漏了 `status_` 尾部的 `_`
符号。

```xml

<io.github.xesam.android.status.MultiStatusView android:id="@+id/multiStatusView"
    android:layout_width="match_parent" android:layout_height="match_parent"
    app:defaultStatus="loading" app:statusIdPrefix="status_">

    <!-- 内容状态 -->
    <LinearLayout android:id="@+id/status_content" android:layout_width="match_parent"
        android:layout_height="match_parent">
        <!-- 您的内容布局 -->
    </LinearLayout>

    <!-- 加载状态 -->
    <LinearLayout android:id="@+id/status_loading" android:layout_width="match_parent"
        android:layout_height="match_parent">
        <!-- 您的内容布局 -->
    </LinearLayout>

    <!-- 空数据状态 -->
    <LinearLayout android:id="@+id/status_empty" android:layout_width="match_parent"
        android:layout_height="match_parent">
        <!-- 您的内容布局 -->
    </LinearLayout>

    <!-- 错误状态 -->
    <LinearLayout android:id="@+id/status_error" android:layout_width="match_parent"
        android:layout_height="match_parent">
        <!-- 您的内容布局 -->
    </LinearLayout>

</io.github.xesam.android.status.MultiStatusView>
```

```kotlin
// 在Activity中
val multiStatusView = findViewById<MultiStatusView>(R.id.multiStatusView)

// 切换状态
multiStatusView.setStatus("loading")  // 显示加载状态
multiStatusView.setStatus("content")  // 显示内容状态
```

#### 资源ID方式（页面内已有View）

```kotlin
val multiStatusView = findViewById<MultiStatusView>(R.id.multiStatusView)

// 注册已存在的视图
multiStatusView
    .registerStatusByViewId("content", R.id.contentLayout)
    .registerStatusByViewId("loading", R.id.loadingLayout)
    .registerStatusByViewId("empty", R.id.emptyLayout)
    .registerStatusByViewId("error", R.id.errorLayout)

// 切换状态
multiStatusView.setStatus("loading")
```

#### 布局资源方式（动态加载View）

```kotlin
val multiStatusView = findViewById<MultiStatusView>(R.id.multiStatusView)

// 注册布局资源
multiStatusView
    .registerStatusByLayout("loading", R.layout.layout_loading)
    .registerStatusByLayout("empty", R.layout.layout_empty) { view ->
        // 初始化视图，如设置点击事件
        view.findViewById<Button>(R.id.retryButton).setOnClickListener {
            // 重试逻辑
        }
    }
    .registerStatusByLayout("error", R.layout.layout_error)

// 切换状态
multiStatusView.setStatus("empty")
```

## API参考

### 核心方法

```kotlin
// 状态切换
fun setStatus(status: String): MultiStatusView
fun getCurrentStatus(): String

// 视图注册
fun registerStatus(status: String, view: View): MultiStatusView
fun registerStatusByViewId(status: String, @IdRes viewId: Int): MultiStatusView
fun registerStatusByLayout(status: String, @LayoutRes layoutRes: Int): MultiStatusView

// 监听器
fun addOnStatusChangeListener(listener: (oldStatus: String, newStatus: String) -> Unit): MultiStatusView
fun setOnStatusNotFoundListener(listener: (String) -> Unit): MultiStatusView
```

### XML属性

```xml

<declare-styleable name="MultiStatusView">
    <!-- 状态ID前缀，默认"status_" -->
    <attr name="statusIdPrefix" format="string" />
    <!-- 默认状态，默认"content" -->
    <attr name="defaultStatus" format="string" />
    <!-- 调试模式，默认false -->
    <attr name="debugMode" format="boolean" />
</declare-styleable>
```

## 演示应用

应用包含以下演示页面：

1. **XML内嵌方式演示** - 展示XML直接声明状态子组件
2. **资源ID方式演示** - 展示代码注册已存在视图
3. **布局资源方式演示** - 展示代码注册布局资源
4. **混合配置方式演示** - 展示多种方式混合使用
5. **RelativeLayout版本演示** - 展示基于RelativeLayout的自定义实现
6. **高级功能演示** - 展示监听器、错误处理等高级功能
7. **StatusViewManager演示** - 展示非侵入式状态管理器的使用
8. **StateManager演示** - 展示通用泛型状态管理器的使用

## 使用示例

### 网络请求场景

```kotlin
private fun loadData() {
    multiStatusView.setStatus("loading")

    viewModel.loadData().observe(this) { result ->
        when (result) {
            is Success -> {
                if (result.data.isEmpty()) {
                    multiStatusView.setStatus("empty")
                } else {
                    multiStatusView.setStatus("content")
                    // 更新UI
                }
            }
            is Error -> {
                multiStatusView.setStatus("error")
            }
        }
    }
}
```

### 状态切换监听

```kotlin
multiStatusView.addOnStatusChangeListener { oldStatus, newStatus ->
    Log.d("MultiStatusView", "状态切换: $oldStatus → $newStatus")
    // 执行相关逻辑，如埋点、动画等
}
```

## 自定义 MultiStatus 容器

### MultiStatusHelper + 委托模式

MultiStatusView采用**委托模式**架构，核心功能由`MultiStatusHelper`实现，与具体视图容器解耦：

```
MultiStatusView (FrameLayout)  ← 委托 →  MultiStatusHelper
     ↑                                              ↑
     └─ 原始实现，保持兼容                        └─ 核心逻辑，容器无关

RelativeMultiStatusView (RelativeLayout) ← 委托 →  MultiStatusHelper
     ↑                                              ↑
     └─ 新实现，展示扩展性                        └─ 复用相同核心逻辑
```

### 基于RelativeLayout的实现（已提供）

```kotlin
// 使用方式与默认版本完全一致
val multiStatusView = findViewById<RelativeMultiStatusView>(R.id.relativeMultiStatusView)
multiStatusView.setStatus("loading")
```

### 自定义任意容器布局

轻松基于任意ViewGroup创建自己的MultiStatusView：

```java
public class MyLinearMultiStatusView extends LinearLayout {
    private MultiStatusHelper helper;

    public MyLinearMultiStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        helper = new MultiStatusHelper(this, context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        helper.autoDiscoverChildViews(); // 自动发现子视图
    }

    // 委托所有方法给helper
    public MyLinearMultiStatusView setStatus(String status) {
        helper.setStatus(status);
        return this;
    }

    // ... 其他方法同样委托给helper
}
```

## 高级配置

### 混合使用多种模式

```kotlin
// XML自动发现 + 代码注册
val multiStatusView = findViewById<MultiStatusView>(R.id.multiStatusView)

// 添加额外的自定义状态
multiStatusView.registerStatusByLayout("custom_loading", R.layout.custom_loading)

// 所有状态都可以正常使用
multiStatusView.setStatus("custom_loading")
```

### 状态别名

```kotlin
// 为状态设置别名，便于状态管理和复用
multiStatusView
    .addStatusAlias("network_error", "error")
    .addStatusAlias("server_error", "error")

// 使用别名切换状态
multiStatusView.setStatus("network_error")  // 实际显示error状态
```

## StatusViewManager

### 什么是 StatusViewManager

`StatusViewManager` 是一个轻量级的状态视图管理器，用于非侵入式地管理和切换多个状态视图。它与 `MultiStatusView` 不同，不需要替换现有组件，只是作为管理器管理现有视图。

### 特点

- **非侵入式**：不需要替换现有组件，只是作为管理器管理现有视图
- **轻量级**：核心功能聚焦于状态切换，API简洁易用
- **内存安全**：使用弱引用管理视图，避免内存泄漏
- **泛型支持**：支持任意类型的状态，如 String、Enum、Integer 等
- **向后兼容**：与 MultiStatusView 类似的API设计，便于用户理解和使用
- **零依赖**：仅依赖Android SDK，无第三方库依赖

### 使用场景

- **现有页面不容易更换组件的情况**
- **需要简单的状态控制功能**
- **希望保持现有布局结构不变的场景**
- **宿主页面动态增减视图的场景**

### 基本使用

#### 1. 创建并配置 StatusViewManager

```kotlin
// 创建管理器
val manager = StatusViewManager<String>()

// 注册状态和对应的视图
manager
    .registerStatus("content", contentView)
    .registerStatus("loading", loadingView)
    .registerStatus("empty", emptyView)
    .registerStatus("error", errorView)

// 切换状态
manager.setStatus("loading")
// 加载完成后
manager.setStatus("content")
```

#### 2. 状态监听

```kotlin
manager.addOnStatusChangeListener { oldStatus, newStatus ->
    Log.d("StatusViewManager", "状态切换: $oldStatus → $newStatus")
    // 执行相关逻辑，如埋点、动画等
}
```

#### 3. 错误处理

```kotlin
manager.setOnStatusNotFoundListener { status ->
    Log.e("StatusViewManager", "状态未找到或视图已回收: $status")
    Toast.makeText(context, "状态未注册或视图已回收", Toast.LENGTH_SHORT).show()
}
```

### API参考

#### 核心方法

```kotlin
// 状态管理
fun <S> registerStatus(status: S, view: View): StatusViewManager<S>
fun <S> setStatus(status: S): StatusViewManager<S>
fun <S> getCurrentStatus(): S?
fun <S> getViewForStatus(status: S): View?
fun <S> getRegisteredStatuses(): List<S>

// 监听器
fun <S> addOnStatusChangeListener(listener: (oldStatus: S?, newStatus: S?) -> Unit): StatusViewManager<S>
fun <S> setOnStatusNotFoundListener(listener: (S) -> Unit): StatusViewManager<S>
```

### 与 MultiStatusView 的区别

| 特性 | StatusViewManager | MultiStatusView |
|------|-------------------|----------------|
| 实现方式 | 非侵入式，管理现有视图 | 容器式，替换现有布局 |
| 内存管理 | 使用弱引用，避免内存泄漏 | 使用强引用 |
| 配置方式 | 仅代码注册 | XML约定、代码注册多种方式 |
| 泛型支持 | 支持任意状态类型 | 仅支持 String |
| 适用场景 | 现有页面不容易更换组件 | 新页面或容易更换组件的场景 |
| 灵活性 | 更高，不影响现有布局 | 较低，需要使用特定容器 |

### 网络请求场景示例

```kotlin
private fun loadData() {
    manager.setStatus("loading")

    viewModel.loadData().observe(this) { result ->
        when (result) {
            is Success -> {
                if (result.data.isEmpty()) {
                    manager.setStatus("empty")
                } else {
                    manager.setStatus("content")
                    // 更新UI
                }
            }
            is Error -> {
                manager.setStatus("error")
            }
        }
    }
}
```

## StateManager

### 什么是 StateManager

`StateManager<S, T>` 是一个通用的泛型状态管理器，与 `StatusViewManager` 不同，它不局限于 View，可以管理任意类型的目标对象。

### 特点

- **泛型设计**：状态类型 S 和目标类型 T 都可以自定义
- **灵活应用**：通过 `StateApplier` 定义任意状态应用逻辑
- **状态别名**：支持为状态设置别名，便于复用
- **状态监听**：支持状态变化监听

### 使用场景

- **UI组件状态管理**：如按钮的启用/禁用/加载/错误状态
- **数据模型状态**：如表单的填写中/验证中/成功/失败状态
- **任意对象的状态切换**：任何需要状态管理的场景

### 基本使用

#### 1. 创建 StateManager

```kotlin
// 定义状态枚举
enum class ButtonState {
    ENABLED,
    DISABLED,
    LOADING,
    ERROR
}

// 创建 StateManager，状态类型为 ButtonState，目标类型为 Button
val stateManager = StateManager<ButtonState, Button>(myButton)
```

#### 2. 注册状态和应用器

```kotlin
stateManager
    .registerState(ButtonState.ENABLED, StateApplier { button ->
        button.isEnabled = true
        button.text = "提交"
        button.alpha = 1.0f
    })
    .registerState(ButtonState.DISABLED, StateApplier { button ->
        button.isEnabled = false
        button.text = "不可点击"
        button.alpha = 0.5f
    })
    .registerState(ButtonState.LOADING, StateApplier { button ->
        button.isEnabled = false
        button.text = "加载中..."
    })
    .registerState(ButtonState.ERROR, StateApplier { button ->
        button.isEnabled = false
        button.text = "出错了"
    })
```

#### 3. 切换状态

```kotlin
stateManager.switchToState(ButtonState.LOADING)

// 模拟加载完成
handler.postDelayed({
    stateManager.switchToState(ButtonState.ENABLED)
}, 2000)
```

### API参考

```kotlin
// 创建
fun <S, T> StateManager(target: T)

// 注册状态
fun registerState(state: S, stateApplier: StateApplier<T>): StateManager<S, T>

// 切换状态
fun switchToState(state: S): StateManager<S, T>

// 获取当前状态
fun currentStateName: S?

// 获取已注册状态
fun registeredStates: Set<S>

// 状态监听
fun setOnStatusChangeListener(listener: (oldState: S?, newState: S?) -> Unit): StateManager<S, T>

// 状态别名
fun addStateAlias(alias: S, originalState: S): StateManager<S, T>
```

## 兼容性

- **最低API级别**：19 (Android 4.4)
- **推荐API级别**：21+ (Android 5.0+)
- **支持AndroidX**：支持

## 状态调试

启用调试模式查看详细日志：

```xml

<io.github.xesam.android.status.MultiStatusView android:id="@+id/multiStatusView"
    android:layout_width="match_parent" android:layout_height="match_parent" app:debugMode="true" />
```

## 📄 许可证

```
Copyright 2025 MultiStatusView

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 贡献

欢迎提交Issue和Pull Request来改进这个项目！
