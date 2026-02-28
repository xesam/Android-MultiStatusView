package io.github.xesam.android.views.status;

/**
 * 状态应用器接口
 * 用于封装不同状态下的目标对象行为
 *
 * @param <T> 目标对象类型
 */
@FunctionalInterface
public interface StateApplier<T> {
    /**
     * 应用状态到目标对象
     *
     * @param target 目标对象
     */
    void applyState(T target);
}
