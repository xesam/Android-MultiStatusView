package io.github.xesam.android.views.status;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * StateViewWrapper 单元测试（纯 Java 版本，不依赖 Android）
 */
public class StateManagerTestPure {

    private TestModel testModel;
    private StateManager<String, TestModel> wrapper;

    @Before
    public void setUp() {
        testModel = new TestModel();
        wrapper = new StateManager<>(testModel);
    }

    @Test
    public void testRegisterState() {
        // 创建测试状态
        StateApplier<TestModel> stateA = target -> {
            target.setValue("State A");
            target.setNumber(100);
        };

        // 注册状态
        wrapper.registerState("stateA", stateA);

        // 验证状态已注册
        assertTrue(wrapper.getRegisteredStates().contains("stateA"));
        assertEquals("stateA", wrapper.getCurrentStateName());
        assertEquals("State A", testModel.getValue());
        assertEquals(100, testModel.getNumber());
    }

    @Test
    public void testSwitchState() {
        // 创建测试状态
        StateApplier<TestModel> stateA = target -> target.setValue("State A");
        StateApplier<TestModel> stateB = target -> target.setValue("State B");

        // 注册状态
        wrapper.registerState("stateA", stateA).registerState("stateB", stateB);

        // 切换状态
        wrapper.switchToState("stateB");

        // 验证状态已切换
        assertEquals("stateB", wrapper.getCurrentStateName());
        assertEquals("State B", testModel.getValue());
    }

    @Test
    public void testStateAlias() {
        // 创建测试状态
        StateApplier<TestModel> stateB = target -> target.setValue("State B");

        // 注册状态并添加别名
        wrapper.registerState("stateB", stateB).addStateAlias("aliasB", "stateB");

        // 使用别名切换状态
        wrapper.switchToState("aliasB");

        // 验证状态已切换
        assertEquals("stateB", wrapper.getCurrentStateName());
        assertEquals("State B", testModel.getValue());
    }

    @Test
    public void testStateChangedListener() {
        // 创建测试状态
        StateApplier<TestModel> stateA = target -> target.setValue("State A");
        StateApplier<TestModel> stateB = target -> target.setValue("State B");

        // 注册状态
        wrapper.registerState("stateA", stateA).registerState("stateB", stateB);

        // 记录监听器调用
        final String[] oldState = new String[1];
        final String[] newState = new String[1];

        // 设置监听器
        wrapper.setOnStatusChangeListener((oldStateName, newStateName) -> {
            oldState[0] = oldStateName;
            newState[0] = newStateName;
        });

        // 切换状态
        wrapper.switchToState("stateB");

        // 验证监听器被调用
        assertEquals("stateA", oldState[0]);
        assertEquals("stateB", newState[0]);
    }

    @Test
    public void testNonExistentState() {
        // 创建测试状态
        StateApplier<TestModel> stateA = target -> target.setValue("State A");

        // 注册状态
        wrapper.registerState("stateA", stateA);

        // 尝试切换到不存在的状态
        wrapper.switchToState("non_existent");

        // 验证状态未改变
        assertEquals("stateA", wrapper.getCurrentStateName());
        assertEquals("State A", testModel.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullState() {
        // 尝试注册 null 状态
        wrapper.registerState("test", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullAlias() {
        // 创建测试状态
        StateApplier<TestModel> stateA = target -> {};

        // 注册状态
        wrapper.registerState("stateA", stateA);

        // 尝试添加 null 别名
        wrapper.addStateAlias(null, "stateA");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOriginalStateName() {
        // 创建测试状态
        StateApplier<TestModel> stateA = target -> {};

        // 注册状态
        wrapper.registerState("stateA", stateA);

        // 尝试添加 null 原始状态名称
        wrapper.addStateAlias("alias", null);
    }

    /**
     * 测试用的简单模型类
     */
    private static class TestModel {
        private String value;
        private int number;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }
}
