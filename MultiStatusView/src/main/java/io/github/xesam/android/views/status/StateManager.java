package io.github.xesam.android.views.status;

import androidx.annotation.NonNull;

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
public class StateManager<S, T> {

    private final Map<S, StateApplier<T>> states = new HashMap<>();

    private S currentStateName;

    private OnStatusChangeListener<S> onStatusChangeListener;

    private final Map<S, S> stateAliases = new HashMap<>();

    private final T target;

    public StateManager(@NonNull T target) {
        this.target = target;
    }

    @NonNull
    public StateManager<S, T> registerState(@NonNull S stateName, @NonNull StateApplier<T> stateApplier) {
        if (stateName == null) {
            throw new IllegalArgumentException("State name cannot be null");
        }
        if (stateApplier == null) {
            throw new IllegalArgumentException("State applier cannot be null");
        }
        states.put(stateName, stateApplier);
        if (currentStateName == null) {
            switchToState(stateName);
        }
        return this;
    }

    @NonNull
    public StateManager<S, T> switchToState(@NonNull S stateName) {
        S actualStateName = stateAliases.containsKey(stateName) ? stateAliases.get(stateName) : stateName;
        StateApplier<T> stateApplier = states.get(actualStateName);
        if (stateApplier != null) {
            S oldStateName = currentStateName;
            currentStateName = actualStateName;
            stateApplier.applyState(target);
            if (onStatusChangeListener != null) {
                onStatusChangeListener.onStatusChange(oldStateName, actualStateName);
            }
        }
        return this;
    }

    public StateApplier<T> getCurrentState() {
        return currentStateName != null ? states.get(currentStateName) : null;
    }

    public S getCurrentStateName() {
        return currentStateName;
    }

    public Set<S> getRegisteredStates() {
        return states.keySet();
    }

    @NonNull
    public StateManager<S, T> setOnStatusChangeListener(@NonNull OnStatusChangeListener<S> listener) {
        this.onStatusChangeListener = listener;
        return this;
    }

    @NonNull
    public StateManager<S, T> addStateAlias(@NonNull S alias, @NonNull S originalStateName) {
        if (alias == null || originalStateName == null) {
            throw new IllegalArgumentException("Alias and original state name cannot be null");
        }
        stateAliases.put(alias, originalStateName);
        return this;
    }
}
