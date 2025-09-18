package io.github.xesam.android.views.status;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.xesam.android.status.R;

/**
 * MultiStatusHelper 是MultiStatusView的核心功能实现类
 * 负责状态管理、视图切换等核心逻辑
 * 通过委托模式与具体的视图容器解耦
 */
public class MultiStatusHelper {

    private static final String TAG = "MultiStatusHelper";
    private static final String DEFAULT_STATUS_ID_PREFIX = "status_";

    // 配置参数
    private String statusIdPrefix = DEFAULT_STATUS_ID_PREFIX;
    private String defaultStatus = "content";
    private boolean debugMode = false;

    // 状态管理
    private final Map<String, View> statusViews = new HashMap<>();
    private final List<OnStatusChangeListener> statusChangeListeners = new ArrayList<>();
    private String currentStatus = "";
    private View currentView = null;

    // 异常处理
    private OnStatusNotFoundListener onStatusNotFoundListener;
    private ErrorHandler errorHandler;

    // 视图容器引用
    private final ViewGroup containerView;
    private final Context context;

    /**
     * 状态未找到监听器接口
     */
    public interface OnStatusNotFoundListener {
        void onStatusNotFound(String status);
    }

    /**
     * 错误处理器接口
     */
    public interface ErrorHandler {
        void onError(Exception exception);
    }

    /**
     * 布局资源创建回调接口
     */
    public interface OnViewCreatedListener {
        void onViewCreated(View view);
    }

    public MultiStatusHelper(@NonNull ViewGroup containerView, @Nullable Context context, @Nullable AttributeSet attrs) {
        this.containerView = containerView;
        this.context = context != null ? context : containerView.getContext();
        initAttributes(attrs);
        currentStatus = defaultStatus;
        if (debugMode) {
            Log.d(TAG, "Initialized with default status: " + defaultStatus);
        }
    }

    /**
     * 初始化自定义属性
     */
    private void initAttributes(@Nullable AttributeSet attrs) {
        if (attrs != null && context != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MultiStatusView);
            try {
                statusIdPrefix = typedArray.getString(R.styleable.MultiStatusView_statusIdPrefix);
                if (statusIdPrefix == null) {
                    statusIdPrefix = DEFAULT_STATUS_ID_PREFIX;
                }

                defaultStatus = typedArray.getString(R.styleable.MultiStatusView_defaultStatus);
                if (defaultStatus == null) {
                    defaultStatus = "content";
                }

                debugMode = typedArray.getBoolean(R.styleable.MultiStatusView_debugMode, false);
            } finally {
                typedArray.recycle();
            }
        }
    }

    /**
     * 自动发现子视图机制（XML内嵌方式）
     * 遍历所有直接子视图，根据ID前缀和状态名进行匹配
     */
    public void autoDiscoverChildViews() {
        if (containerView.getChildCount() == 0) return;

        if (debugMode) {
            Log.d(TAG, "Auto-discovering child views with prefix: " + statusIdPrefix);
        }

        // 获取所有子视图并创建映射
        Map<String, View> childViews = new HashMap<>();
        for (int i = 0; i < containerView.getChildCount(); i++) {
            View child = containerView.getChildAt(i);
            int viewId = child.getId();

            if (viewId != View.NO_ID) {
                try {
                    String idResName = context.getResources().getResourceName(viewId);
                    String idName = idResName.substring(idResName.indexOf('/') + 1);

                    if (debugMode) {
                        Log.d(TAG, "Resource name: " + idResName + ", idName: " + idName);
                    }

                    if (idName.startsWith(statusIdPrefix)) {
                        String statusName = idName.substring(statusIdPrefix.length());
                        childViews.put(statusName, child);

                        if (debugMode) {
                            Log.d(TAG, "Discovered status view: " + statusName);
                        }
                    }
                } catch (Exception e) {
                    if (debugMode) {
                        Log.w(TAG, "Failed to get resource name for view ID: " + viewId, e);
                    }
                }
            }
        }

        // 注册发现的状态视图
        for (Map.Entry<String, View> entry : childViews.entrySet()) {
            registerStatusInternal(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 注册状态视图（内部方法）
     */
    private void registerStatusInternal(String status, View view) {
        statusViews.put(status, view);
        if (status.equals(currentStatus)) {
            view.setVisibility(View.VISIBLE);
            currentView = view;
        } else {
            view.setVisibility(View.GONE);
        }

        if (debugMode) {
            Log.d(TAG, "Registered status: " + status);
        }
    }

    /**
     * 模式2：资源ID方式 - 注册已存在的视图
     */
    @NonNull
    public MultiStatusHelper registerStatus(String status, View view) {
        registerStatusInternal(status, view);
        return this;
    }

    /**
     * 模式2：资源ID方式 - 通过视图ID注册
     */
    @NonNull
    public MultiStatusHelper registerStatusByViewId(String status, @IdRes int viewId) {
        View view = containerView.findViewById(viewId);
        if (view == null) {
            throw new IllegalArgumentException("View with ID " + viewId + " not found");
        }
        return registerStatus(status, view);
    }

    /**
     * 模式3：布局资源方式 - 注册布局资源
     */
    @NonNull
    public MultiStatusHelper registerStatusByLayout(String status, @LayoutRes int layoutRes) {
        return registerStatusByLayout(status, layoutRes, null);
    }

    /**
     * 注册布局资源（简化方法，无缓存逻辑）
     */
    @NonNull
    public MultiStatusHelper registerLayoutResource(@NonNull String status, @LayoutRes int layoutResource) {
        if (debugMode) {
            Log.d(TAG, "Registering layout resource: " + status + " -> " + layoutResource);
        }

        try {
            View view = LayoutInflater.from(context).inflate(layoutResource, containerView, false);
            statusViews.put(status, view);

            if (debugMode) {
                Log.d(TAG, "Successfully registered layout resource: " + status);
            }
        } catch (Exception e) {
            if (debugMode) {
                Log.e(TAG, "Error inflating layout resource: " + layoutResource, e);
            }
            if (errorHandler != null) {
                errorHandler.onError(e);
            }
        }

        return this;
    }

    /**
     * 模式3：布局资源方式 - 带回调的注册（简化方法，无缓存逻辑）
     */
    @NonNull
    public MultiStatusHelper registerStatusByLayout(String status, @LayoutRes int layoutRes, @Nullable OnViewCreatedListener onViewCreated) {
        View view = LayoutInflater.from(context).inflate(layoutRes, containerView, false);
        containerView.addView(view);
        if (onViewCreated != null) {
            onViewCreated.onViewCreated(view);
        }
        return registerStatus(status, view);
    }

    /**
     * 切换到指定状态
     */
    @NonNull
    public MultiStatusHelper setStatus(String status) {
        if (status.equals(currentStatus)) {
            if (debugMode) {
                Log.d(TAG, "Status unchanged: " + status);
            }
            return this;
        }

        View targetView = statusViews.get(status);
        if (targetView == null) {
            handleStatusNotFound(status);
            return this;
        }

        String oldStatus = currentStatus;
        View oldView = currentView;

        // 隐藏当前视图
        if (oldView != null) {
            oldView.setVisibility(View.GONE);
        }

        // 显示目标视图
        targetView.setVisibility(View.VISIBLE);
        currentStatus = status;
        currentView = targetView;

        if (debugMode) {
            Log.d(TAG, "Status changed from " + oldStatus + " to " + status);
        }

        // 触发监听器
        notifyStatusChange(oldStatus, status);

        return this;
    }

    /**
     * 获取当前状态
     */
    @NonNull
    public String getCurrentStatus() {
        return currentStatus;
    }

    /**
     * 获取指定状态的视图
     */
    @Nullable
    public View getViewForStatus(String status) {
        return statusViews.get(status);
    }

    /**
     * 获取所有已注册的状态
     */
    @NonNull
    public List<String> getRegisteredStatuses() {
        return new ArrayList<>(statusViews.keySet());
    }

    /**
     * 添加状态变化监听器
     */
    @NonNull
    public MultiStatusHelper addOnStatusChangeListener(@NonNull OnStatusChangeListener listener) {
        if (!statusChangeListeners.contains(listener)) {
            statusChangeListeners.add(listener);
        }
        return this;
    }

    /**
     * 移除状态变化监听器
     */
    @NonNull
    public MultiStatusHelper removeOnStatusChangeListener(@NonNull OnStatusChangeListener listener) {
        statusChangeListeners.remove(listener);
        return this;
    }

    /**
     * 移除所有状态变化监听器
     */
    @NonNull
    public MultiStatusHelper removeAllStatusChangeListeners() {
        statusChangeListeners.clear();
        return this;
    }

    /**
     * 设置状态未找到监听器
     */
    @NonNull
    public MultiStatusHelper setOnStatusNotFoundListener(@Nullable OnStatusNotFoundListener listener) {
        onStatusNotFoundListener = listener;
        return this;
    }

    /**
     * 设置错误处理器
     */
    @NonNull
    public MultiStatusHelper setErrorHandler(@Nullable ErrorHandler handler) {
        errorHandler = handler;
        return this;
    }

    /**
     * 处理状态未找到的情况
     */
    private void handleStatusNotFound(String status) {
        if (debugMode) {
            Log.w(TAG, "Status not found: " + status);
        }

        if (onStatusNotFoundListener != null) {
            onStatusNotFoundListener.onStatusNotFound(status);
        }

        // 默认行为：保持当前状态
        if (debugMode) {
            Log.d(TAG, "Keeping current status: " + currentStatus);
        }
    }

    /**
     * 通知状态变化监听器
     */
    private void notifyStatusChange(String oldStatus, String newStatus) {
        for (OnStatusChangeListener listener : statusChangeListeners) {
            try {
                listener.onStatusChange(oldStatus, newStatus);
            } catch (Exception e) {
                if (debugMode) {
                    Log.e(TAG, "Error notifying status change listener", e);
                }
                if (errorHandler != null) {
                    errorHandler.onError(e);
                }
            }
        }
    }

    /**
     * 添加状态别名
     */
    @NonNull
    public MultiStatusHelper addStatusAlias(String alias, String originalStatus) {
        View originalView = statusViews.get(originalStatus);
        if (originalView != null) {
            statusViews.put(alias, originalView);

            if (debugMode) {
                Log.d(TAG, "Added status alias: " + alias + " -> " + originalStatus);
            }
        } else {
            if (debugMode) {
                Log.w(TAG, "Cannot create alias for non-existent status: " + originalStatus);
            }
        }
        return this;
    }
}