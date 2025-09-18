package io.github.xesam.android.views.status;

/**
 * 状态变化监听器接口
 * 用于监听MultiStatusView中状态的变化
 */
public interface OnStatusChangeListener {
    /**
     * 当状态发生变化时调用
     * 
     * @param oldStatus 旧的状态名称
     * @param newStatus 新的状态名称
     */
    void onStatusChange(String oldStatus, String newStatus);
}