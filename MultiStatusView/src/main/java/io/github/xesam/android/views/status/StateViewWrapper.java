package io.github.xesam.android.views.status;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 状态包装器
 * 通过泛型支持不同类型的状态名称和目标对象
 * 负责注册和切换状态
 *
 * @param <S> 状态名称类型
 * @param <T> 目标对象类型
 */
public class StateViewWrapper<S, T> {

    // 存储注册的状态应用器
    private final Map<S, StateApplier<T>> states = new HashMap<>();

    // 当前状态名称
    private S currentStateName;

    // 状态切换监听器
    private OnStateChangedListener<S, T> onStateChangedListener;

    // 状态别名映射
    private final Map<S, S> stateAliases = new HashMap<>();

    // 目标对象
    private final T target;

    /**
     * 构造函数
     *
     * @param target 目标对象
     */
    public StateViewWrapper(T target) {
        this.target = target;
    }

    /**
     * 注册状态应用器
     *
     * @param stateName 状态名称
     * @param stateApplier 要注册的状态应用器
     * @return 当前实例，支持链式调用
     */
    public StateViewWrapper<S, T> registerState(S stateName, StateApplier<T> stateApplier) {
        if (stateName == null) {
            throw new IllegalArgumentException("State name cannot be null");
        }
        if (stateApplier == null) {
            throw new IllegalArgumentException("State applier cannot be null");
        }
        states.put(stateName, stateApplier);
        // 如果是第一个注册的状态，自动设为当前状态
        if (currentStateName == null) {
            switchToState(stateName);
        }
        return this;
    }

    /**
     * 切换到指定状态
     *
     * @param stateName 状态名称
     * @return 当前实例，支持链式调用
     */
    public StateViewWrapper<S, T> switchToState(S stateName) {
        // 解析别名
        S actualStateName = stateAliases.getOrDefault(stateName, stateName);
        StateApplier<T> stateApplier = states.get(actualStateName);
        if (stateApplier != null) {
            S oldStateName = currentStateName;
            currentStateName = actualStateName;
            stateApplier.applyState(target);
            // 触发监听器
            if (onStateChangedListener != null) {
                onStateChangedListener.onStateChanged(oldStateName, actualStateName);
            }
        }
        return this;
    }

    /**
     * 获取当前状态应用器
     *
     * @return 当前状态应用器
     */
    public StateApplier<T> getCurrentState() {
        return currentStateName != null ? states.get(currentStateName) : null;
    }

    /**
     * 获取当前状态名称
     *
     * @return 当前状态名称
     */
    public S getCurrentStateName() {
        return currentStateName;
    }

    /**
     * 获取所有注册的状态名称
     *
     * @return 状态名称集合
     */
    public Set<S> getRegisteredStates() {
        return states.keySet();
    }

    /**
     * 设置状态切换监听器
     *
     * @param listener 状态切换监听器
     * @return 当前实例，支持链式调用
     */
    public StateViewWrapper<S, T> setOnStateChangedListener(OnStateChangedListener<S, T> listener) {
        this.onStateChangedListener = listener;
        return this;
    }

    /**
     * 添加状态别名
     *
     * @param alias           状态别名
     * @param originalStateName 原始状态名称
     * @return 当前实例，支持链式调用
     */
    public StateViewWrapper<S, T> addStateAlias(S alias, S originalStateName) {
        if (alias == null || originalStateName == null) {
            throw new IllegalArgumentException("Alias and original state name cannot be null");
        }
        stateAliases.put(alias, originalStateName);
        return this;
    }

    /**
     * 状态切换监听器接口
     *
     * @param <S> 状态名称类型
     * @param <T> 目标对象类型
     */
    public interface OnStateChangedListener<S, T> {
        /**
         * 当状态发生变化时调用
         *
         * @param oldStateName 旧状态名称
         * @param newStateName 新状态名称
         */
        void onStateChanged(S oldStateName, S newStateName);
    }
}
