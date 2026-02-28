package io.github.xesam.android.views.status;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StatusCoordinator 是一个轻量级的状态协调器
 * 用于非侵入式地管理和切换多个状态视图
 * 
 * 特点：
 * 1. 非侵入式：不需要替换现有组件，只是作为协调者管理现有视图
 * 2. 轻量级：核心功能聚焦于状态切换，API简洁易用
 * 3. 兼容性：与 MultiStatusView 类似的API设计，便于用户理解和使用
 * 4. 内存安全：使用弱引用管理视图，避免内存泄漏
 * 
 * 使用场景：
 * - 现有页面不容易更换组件的情况
 * - 需要简单的状态控制功能
 * - 希望保持现有布局结构不变的场景
 */
public class StatusCoordinator {

    // 状态管理 - 使用弱引用避免内存泄漏
    private final Map<String, WeakReference<View>> statusViews = new HashMap<>();
    private final List<OnStatusChangeListener> statusChangeListeners = new ArrayList<>();
    private String currentStatus = "";
    private WeakReference<View> currentViewRef = null;

    // 异常处理
    private OnStatusNotFoundListener onStatusNotFoundListener;

    /**
     * 状态未找到监听器接口
     */
    public interface OnStatusNotFoundListener {
        void onStatusNotFound(String status);
    }

    /**
     * 注册状态和对应的视图
     * 
     * @param status 状态名称
     * @param view   对应的视图
     * @return 当前实例，支持链式调用
     */
    @NonNull
    public StatusCoordinator registerStatus(String status, View view) {
        if (view == null) {
            throw new IllegalArgumentException("View cannot be null");
        }
        
        statusViews.put(status, new WeakReference<>(view));
        
        // 初始状态处理
        if (currentStatus.isEmpty()) {
            currentStatus = status;
            currentViewRef = new WeakReference<>(view);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        
        return this;
    }

    /**
     * 切换到指定状态
     * 
     * @param status 目标状态
     * @return 当前实例，支持链式调用
     */
    @NonNull
    public StatusCoordinator setStatus(String status) {
        if (status.equals(currentStatus)) {
            return this;
        }

        // 获取目标视图的弱引用
        WeakReference<View> targetViewRef = statusViews.get(status);
        View targetView = targetViewRef != null ? targetViewRef.get() : null;
        
        if (targetView == null) {
            handleStatusNotFound(status);
            return this;
        }

        String oldStatus = currentStatus;
        View oldView = currentViewRef != null ? currentViewRef.get() : null;

        // 隐藏当前视图
        if (oldView != null) {
            oldView.setVisibility(View.GONE);
        }

        // 显示目标视图
        targetView.setVisibility(View.VISIBLE);
        currentStatus = status;
        currentViewRef = targetViewRef;

        // 触发监听器
        notifyStatusChange(oldStatus, status);

        return this;
    }

    /**
     * 获取当前状态
     * 
     * @return 当前状态名称
     */
    @NonNull
    public String getCurrentStatus() {
        return currentStatus;
    }

    /**
     * 获取指定状态的视图
     * 
     * @param status 状态名称
     * @return 对应的视图，如果状态不存在或视图已被回收则返回null
     */
    @Nullable
    public View getViewForStatus(String status) {
        WeakReference<View> viewRef = statusViews.get(status);
        return viewRef != null ? viewRef.get() : null;
    }

    /**
     * 获取所有已注册的状态
     * 
     * @return 已注册的状态列表
     */
    @NonNull
    public List<String> getRegisteredStatuses() {
        return new ArrayList<>(statusViews.keySet());
    }

    /**
     * 添加状态切换监听器
     * 
     * @param listener 状态切换监听器
     * @return 当前实例，支持链式调用
     */
    @NonNull
    public StatusCoordinator addOnStatusChangeListener(@NonNull OnStatusChangeListener listener) {
        statusChangeListeners.add(listener);
        return this;
    }

    /**
     * 设置状态未找到监听器
     * 
     * @param listener 状态未找到监听器
     * @return 当前实例，支持链式调用
     */
    @NonNull
    public StatusCoordinator setOnStatusNotFoundListener(@Nullable OnStatusNotFoundListener listener) {
        this.onStatusNotFoundListener = listener;
        return this;
    }

    /**
     * 处理状态未找到的情况
     */
    private void handleStatusNotFound(String status) {
        if (onStatusNotFoundListener != null) {
            onStatusNotFoundListener.onStatusNotFound(status);
        }
    }

    /**
     * 通知状态切换
     */
    private void notifyStatusChange(String oldStatus, String newStatus) {
        for (OnStatusChangeListener listener : statusChangeListeners) {
            listener.onStatusChange(oldStatus, newStatus);
        }
    }
}
