package io.github.xesam.android.views.status;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * MultiStatusView 是一个支持多种状态视图切换的容器组件
 * <p>
 * 支持三种配置模式：
 * 1. XML内嵌方式：在XML中直接声明状态子组件，使用约定命名自动发现
 * 2. 资源ID方式：通过代码注册已存在的视图
 * 3. 布局资源方式：通过代码注册布局资源，自动膨胀并管理
 * <p>
 * 注意：这是Java版本实现，与Kotlin版本功能完全兼容
 * <p>
 * 架构改进：使用委托模式，将核心功能委托给MultiStatusHelper实现
 */
public class MultiStatusView extends FrameLayout {

    private MultiStatusHelper helper;

    public MultiStatusView(@NonNull Context context) {
        super(context);
        initHelper(context, null);
    }

    public MultiStatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initHelper(context, attrs);
    }

    public MultiStatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHelper(context, attrs);
    }

    private void initHelper(Context context, AttributeSet attrs) {
        helper = new MultiStatusHelper(this, context, attrs);
    }

    /**
     * 当XML布局加载完成时调用，用于自动发现子视图
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        helper.autoDiscoverChildViews();
    }

    /**
     * 模式2：资源ID方式 - 注册已存在的视图
     */
    @NonNull
    public MultiStatusView registerStatus(String status, View view) {
        helper.registerStatus(status, view);
        return this;
    }

    /**
     * 模式2：资源ID方式 - 通过视图ID注册
     */
    @NonNull
    public MultiStatusView registerStatusByViewId(String status, @IdRes int viewId) {
        helper.registerStatusByViewId(status, viewId);
        return this;
    }

    /**
     * 模式3：布局资源方式 - 注册布局资源
     */
    @NonNull
    public MultiStatusView registerStatusByLayout(String status, @LayoutRes int layoutRes) {
        helper.registerStatusByLayout(status, layoutRes);
        return this;
    }

    /**
     * 模式3：布局资源方式 - 带回调的注册（简化方法，无缓存逻辑）
     */
    @NonNull
    public MultiStatusView registerStatusByLayout(String status, @LayoutRes int layoutRes, @Nullable MultiStatusHelper.OnViewCreatedListener onViewCreated) {
        helper.registerStatusByLayout(status, layoutRes, onViewCreated);
        return this;
    }

    /**
     * 切换到指定状态
     */
    @NonNull
    public MultiStatusView setStatus(String status) {
        helper.setStatus(status);
        return this;
    }

    /**
     * 获取当前状态
     */
    @NonNull
    public String getCurrentStatus() {
        return helper.getCurrentStatus();
    }

    /**
     * 获取指定状态的视图
     */
    @Nullable
    public View getViewForStatus(String status) {
        return helper.getViewForStatus(status);
    }

    /**
     * 获取所有已注册的状态
     */
    @NonNull
    public List<String> getRegisteredStatuses() {
        return helper.getRegisteredStatuses();
    }

    /**
     * 添加状态变化监听器
     */
    @NonNull
    public MultiStatusView addOnStatusChangeListener(@NonNull OnStatusChangeListener listener) {
        helper.addOnStatusChangeListener(listener);
        return this;
    }

    /**
     * 移除状态变化监听器
     */
    @NonNull
    public MultiStatusView removeOnStatusChangeListener(@NonNull OnStatusChangeListener listener) {
        helper.removeOnStatusChangeListener(listener);
        return this;
    }

    /**
     * 移除所有状态变化监听器
     */
    @NonNull
    public MultiStatusView removeAllStatusChangeListeners() {
        helper.removeAllStatusChangeListeners();
        return this;
    }

    /**
     * 设置状态未找到监听器
     */
    @NonNull
    public MultiStatusView setOnStatusNotFoundListener(@Nullable MultiStatusHelper.OnStatusNotFoundListener listener) {
        helper.setOnStatusNotFoundListener(listener);
        return this;
    }

    /**
     * 设置错误处理器
     */
    @NonNull
    public MultiStatusView setErrorHandler(@Nullable MultiStatusHelper.ErrorHandler handler) {
        helper.setErrorHandler(handler);
        return this;
    }

    /**
     * 添加状态别名
     */
    @NonNull
    public MultiStatusView addStatusAlias(String alias, String originalStatus) {
        helper.addStatusAlias(alias, originalStatus);
        return this;
    }

    /**
     * 获取Helper实例（用于高级用法）
     */
    @NonNull
    protected MultiStatusHelper getHelper() {
        return helper;
    }
}