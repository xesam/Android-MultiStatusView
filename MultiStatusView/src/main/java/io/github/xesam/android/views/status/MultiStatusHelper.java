package io.github.xesam.android.views.status;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.xesam.android.status.R;

public class MultiStatusHelper {

    private static final String DEFAULT_STATUS_ID_PREFIX = "status_";

    private String statusIdPrefix = DEFAULT_STATUS_ID_PREFIX;
    private String defaultStatus = "content";
    private boolean debugMode = false;

    private final StatusCoordinator<String> coordinator;

    private OnStatusNotFoundListener onStatusNotFoundListener;
    private ErrorHandler errorHandler;

    private final ViewGroup containerView;
    private final Context context;

    public interface OnStatusNotFoundListener {
        void onStatusNotFound(String status);
    }

    public interface ErrorHandler {
        void onError(Exception exception);
    }

    public interface OnViewCreatedListener {
        void onViewCreated(View view);
    }

    public MultiStatusHelper(@NonNull ViewGroup containerView, @Nullable Context context, @Nullable AttributeSet attrs) {
        this.containerView = containerView;
        this.context = context != null ? context : containerView.getContext();
        initAttributes(attrs);

        this.coordinator = new StatusCoordinator<String>();

        this.coordinator.setOnStatusNotFoundListener(this::handleStatusNotFound);
    }

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

    public void autoDiscoverChildViews() {
        if (containerView.getChildCount() == 0) return;

        Map<String, View> childViews = new HashMap<>();
        for (int i = 0; i < containerView.getChildCount(); i++) {
            View child = containerView.getChildAt(i);
            int viewId = child.getId();

            if (viewId != View.NO_ID) {
                try {
                    String idResName = context.getResources().getResourceName(viewId);
                    String idName = idResName.substring(idResName.indexOf('/') + 1);

                    if (idName.startsWith(statusIdPrefix)) {
                        String statusName = idName.substring(statusIdPrefix.length());
                        childViews.put(statusName, child);
                    }
                } catch (Exception e) {
                    if (errorHandler != null) {
                        errorHandler.onError(e);
                    }
                }
            }
        }

        for (Map.Entry<String, View> entry : childViews.entrySet()) {
            registerStatus(entry.getKey(), entry.getValue());
        }
    }

    @NonNull
    public MultiStatusHelper registerStatus(@NonNull String status, @NonNull View view) {
        coordinator.registerStatus(status, view);
        return this;
    }

    @NonNull
    public MultiStatusHelper registerStatusByViewId(@NonNull String status, @IdRes int viewId) {
        View view = containerView.findViewById(viewId);
        if (view == null) {
            throw new IllegalArgumentException("View with ID " + viewId + " not found");
        }
        return registerStatus(status, view);
    }

    @NonNull
    public MultiStatusHelper registerStatusByLayout(@NonNull String status, @LayoutRes int layoutRes) {
        return registerStatusByLayout(status, layoutRes, null);
    }

    @NonNull
    public MultiStatusHelper registerLayoutResource(@NonNull String status, @LayoutRes int layoutResource) {
        try {
            View view = LayoutInflater.from(context).inflate(layoutResource, containerView, false);
            registerStatus(status, view);
        } catch (Exception e) {
            if (errorHandler != null) {
                errorHandler.onError(e);
            }
        }

        return this;
    }

    @NonNull
    public MultiStatusHelper registerStatusByLayout(@NonNull String status, @LayoutRes int layoutRes, @Nullable OnViewCreatedListener onViewCreated) {
        View view = LayoutInflater.from(context).inflate(layoutRes, containerView, false);
        containerView.addView(view);
        if (onViewCreated != null) {
            onViewCreated.onViewCreated(view);
        }
        return registerStatus(status, view);
    }

    @NonNull
    public MultiStatusHelper setStatus(@NonNull String status) {
        coordinator.setStatus(status);
        return this;
    }

    @Nullable
    public String getCurrentStatus() {
        return coordinator.getCurrentStatus();
    }

    @Nullable
    public View getViewForStatus(@NonNull String status) {
        return coordinator.getViewForStatus(status);
    }

    @NonNull
    public List<String> getRegisteredStatuses() {
        return coordinator.getRegisteredStatuses();
    }

    @NonNull
    public MultiStatusHelper addOnStatusChangeListener(@NonNull OnStatusChangeListener<String> listener) {
        coordinator.addOnStatusChangeListener((oldStatus, newStatus) -> {
            try {
                listener.onStatusChange(oldStatus, newStatus);
            } catch (Exception e) {
                if (errorHandler != null) {
                    errorHandler.onError(e);
                }
            }
        });
        return this;
    }

    @NonNull
    public MultiStatusHelper removeOnStatusChangeListener(@NonNull OnStatusChangeListener<String> listener) {
        coordinator.removeOnStatusChangeListener(listener);
        return this;
    }

    @NonNull
    public MultiStatusHelper removeAllStatusChangeListeners() {
        coordinator.removeAllStatusChangeListeners();
        return this;
    }

    @NonNull
    public MultiStatusHelper setOnStatusNotFoundListener(@Nullable OnStatusNotFoundListener listener) {
        this.onStatusNotFoundListener = listener;
        return this;
    }

    @NonNull
    public MultiStatusHelper setErrorHandler(@Nullable ErrorHandler handler) {
        this.errorHandler = handler;
        return this;
    }

    private void handleStatusNotFound(String status) {
        if (onStatusNotFoundListener != null) {
            onStatusNotFoundListener.onStatusNotFound(status);
        }
    }

    @NonNull
    public MultiStatusHelper addStatusAlias(@NonNull String alias, @NonNull String originalStatus) {
        View originalView = coordinator.getViewForStatus(originalStatus);
        if (originalView != null) {
            coordinator.registerStatus(alias, originalView);
        }
        return this;
    }

    @NonNull
    protected StatusCoordinator<String> getCoordinator() {
        return coordinator;
    }
}
