package io.github.xesam.android.views.status;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * StatusCoordinator 是一个轻量级的状态协调器
 * 用于非侵入式地管理和切换多个状态视图
 * <p>
 * 特点：
 * 1. 非侵入式：不需要替换现有组件，只是作为协调者管理现有视图
 * 2. 轻量级：核心功能聚焦于状态切换，API简洁易用
 * 3. 兼容性：与 MultiStatusView 类似的API设计，便于用户理解和使用
 * 4. 内存安全：使用弱引用管理视图，避免内存泄漏
 * 5. 泛型支持：支持任意类型的状态，如 String、Enum、Integer 等
 * <p>
 * 使用场景：
 * - 现有页面不容易更换组件的情况
 * - 需要简单的状态控制功能
 * - 希望保持现有布局结构不变的场景
 *
 * @param <S> 状态类型
 */
public class StatusViewManager<S> {

    private final Map<S, WeakReference<View>> statusViews = new HashMap<>();
    private final List<OnStatusChangeListener<S>> statusChangeListeners = new ArrayList<>();
    private S currentStatus = null;
    private WeakReference<View> currentViewRef = null;

    private OnStatusNotFoundListener<S> onStatusNotFoundListener;

    public interface OnStatusNotFoundListener<S> {
        void onStatusNotFound(S status);
    }

    @NonNull
    public StatusViewManager<S> registerStatus(@NonNull S status, @NonNull View view) {
        if (view == null) {
            throw new IllegalArgumentException("View cannot be null");
        }

        statusViews.put(status, new WeakReference<>(view));

        if (currentStatus == null) {
            currentStatus = status;
            currentViewRef = new WeakReference<>(view);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }

        return this;
    }

    @NonNull
    public StatusViewManager<S> setStatus(@NonNull S status) {
        if (Objects.equals(status, currentStatus)) {
            return this;
        }

        WeakReference<View> targetViewRef = statusViews.get(status);
        View targetView = targetViewRef != null ? targetViewRef.get() : null;

        if (targetView == null) {
            handleStatusNotFound(status);
            return this;
        }

        S oldStatus = currentStatus;
        View oldView = currentViewRef != null ? currentViewRef.get() : null;

        if (oldView != null) {
            oldView.setVisibility(View.GONE);
        }

        targetView.setVisibility(View.VISIBLE);
        currentStatus = status;
        currentViewRef = targetViewRef;

        notifyStatusChange(oldStatus, status);

        return this;
    }

    @Nullable
    public S getCurrentStatus() {
        return currentStatus;
    }

    @Nullable
    public View getViewForStatus(@NonNull S status) {
        WeakReference<View> viewRef = statusViews.get(status);
        return viewRef != null ? viewRef.get() : null;
    }

    @NonNull
    public List<S> getRegisteredStatuses() {
        return new ArrayList<>(statusViews.keySet());
    }

    @NonNull
    public StatusViewManager<S> addOnStatusChangeListener(@NonNull OnStatusChangeListener<S> listener) {
        statusChangeListeners.add(listener);
        return this;
    }

    @NonNull
    public StatusViewManager<S> removeOnStatusChangeListener(@NonNull OnStatusChangeListener<S> listener) {
        statusChangeListeners.remove(listener);
        return this;
    }

    @NonNull
    public StatusViewManager<S> removeAllStatusChangeListeners() {
        statusChangeListeners.clear();
        return this;
    }

    @NonNull
    public StatusViewManager<S> setOnStatusNotFoundListener(@Nullable OnStatusNotFoundListener<S> listener) {
        this.onStatusNotFoundListener = listener;
        return this;
    }

    private void handleStatusNotFound(S status) {
        if (onStatusNotFoundListener != null) {
            onStatusNotFoundListener.onStatusNotFound(status);
        }
    }

    private void notifyStatusChange(S oldStatus, S newStatus) {
        for (OnStatusChangeListener<S> listener : statusChangeListeners) {
            listener.onStatusChange(oldStatus, newStatus);
        }
    }
}
