package io.github.xesam.android.views.status.example;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.github.xesam.android.views.status.MultiStatusHelper;
import io.github.xesam.android.views.status.OnStatusChangeListener;

/**
 * RelativeLayout实现的MultiStatusView
 * 展示如何通过MultiStatusHelper快速实现基于不同容器布局的MultiStatusView
 * 
 * 功能与默认的MultiStatusView（基于FrameLayout）完全一致
 * 只是容器布局改为了RelativeLayout
 */
public class RelativeMultiStatusView extends RelativeLayout {

    private MultiStatusHelper helper;

    public RelativeMultiStatusView(@NonNull Context context) {
        super(context);
        initHelper(context, null);
    }

    public RelativeMultiStatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initHelper(context, attrs);
    }

    public RelativeMultiStatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
    public RelativeMultiStatusView registerStatus(String status, View view) {
        helper.registerStatus(status, view);
        return this;
    }

    /**
     * 模式2：资源ID方式 - 通过视图ID注册
     */
    @NonNull
    public RelativeMultiStatusView registerStatusByViewId(String status, @IdRes int viewId) {
        helper.registerStatusByViewId(status, viewId);
        return this;
    }

    /**
     * 模式3：布局资源方式 - 注册布局资源
     */
    @NonNull
    public RelativeMultiStatusView registerStatusByLayout(String status, @LayoutRes int layoutRes) {
        helper.registerStatusByLayout(status, layoutRes);
        return this;
    }

    /**
     * 模式3：布局资源方式 - 带回调的注册（简化方法，无缓存逻辑）
     */
    @NonNull
    public RelativeMultiStatusView registerStatusByLayout(String status, @LayoutRes int layoutRes, @Nullable MultiStatusHelper.OnViewCreatedListener onViewCreated) {
        helper.registerStatusByLayout(status, layoutRes, onViewCreated);
        return this;
    }

    /**
     * 切换到指定状态
     */
    @NonNull
    public RelativeMultiStatusView setStatus(String status) {
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
    public RelativeMultiStatusView addOnStatusChangeListener(@NonNull OnStatusChangeListener listener) {
        helper.addOnStatusChangeListener(listener);
        return this;
    }

    /**
     * 移除状态变化监听器
     */
    @NonNull
    public RelativeMultiStatusView removeOnStatusChangeListener(@NonNull OnStatusChangeListener listener) {
        helper.removeOnStatusChangeListener(listener);
        return this;
    }

    /**
     * 移除所有状态变化监听器
     */
    @NonNull
    public RelativeMultiStatusView removeAllStatusChangeListeners() {
        helper.removeAllStatusChangeListeners();
        return this;
    }

    /**
     * 设置状态未找到监听器
     */
    @NonNull
    public RelativeMultiStatusView setOnStatusNotFoundListener(@Nullable MultiStatusHelper.OnStatusNotFoundListener listener) {
        helper.setOnStatusNotFoundListener(listener);
        return this;
    }

    /**
     * 设置错误处理器
     */
    @NonNull
    public RelativeMultiStatusView setErrorHandler(@Nullable MultiStatusHelper.ErrorHandler handler) {
        helper.setErrorHandler(handler);
        return this;
    }

    /**
     * 添加状态别名
     */
    @NonNull
    public RelativeMultiStatusView addStatusAlias(String alias, String originalStatus) {
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