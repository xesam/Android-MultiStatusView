package io.github.xesam.android.views.status;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * StateViewWrapper 单元测试（简化版，使用 TestView）
 */
public class StateViewWrapperTestSimple {

    private TestView testView;
    private StateViewWrapper<String, TestView> wrapper;

    @Before
    public void setUp() {
        testView = new TestView();
        wrapper = new StateViewWrapper<>(testView);
    }

    @Test
    public void testRegisterState() {
        // 创建测试状态
        StateApplier<TestView> normalState = target -> target.setText("Normal");

        // 注册状态
        wrapper.registerState("normal", normalState);

        // 验证状态已注册
        assertTrue(wrapper.getRegisteredStates().contains("normal"));
        assertEquals("normal", wrapper.getCurrentStateName());
        assertEquals("Normal", testView.getText());
    }

    @Test
    public void testSwitchState() {
        // 创建测试状态
        StateApplier<TestView> normalState = target -> target.setText("Normal");
        StateApplier<TestView> pressedState = target -> target.setText("Pressed");

        // 注册状态
        wrapper.registerState("normal", normalState).registerState("pressed", pressedState);

        // 切换状态
        wrapper.switchToState("pressed");

        // 验证状态已切换
        assertEquals("pressed", wrapper.getCurrentStateName());
        assertEquals("Pressed", testView.getText());
    }

    @Test
    public void testStateAlias() {
        // 创建测试状态
        StateApplier<TestView> pressedState = target -> target.setText("Pressed");

        // 注册状态并添加别名
        wrapper.registerState("pressed", pressedState).addStateAlias("active", "pressed");

        // 使用别名切换状态
        wrapper.switchToState("active");

        // 验证状态已切换
        assertEquals("pressed", wrapper.getCurrentStateName());
        assertEquals("Pressed", testView.getText());
    }

    @Test
    public void testStateChangedListener() {
        // 创建测试状态
        StateApplier<TestView> normalState = target -> target.setText("Normal");
        StateApplier<TestView> pressedState = target -> target.setText("Pressed");

        // 注册状态
        wrapper.registerState("normal", normalState).registerState("pressed", pressedState);

        // 记录监听器调用
        final String[] oldState = new String[1];
        final String[] newState = new String[1];

        // 设置监听器
        wrapper.setOnStateChangedListener((oldStateName, newStateName) -> {
            oldState[0] = oldStateName;
            newState[0] = newStateName;
        });

        // 切换状态
        wrapper.switchToState("pressed");

        // 验证监听器被调用
        assertEquals("normal", oldState[0]);
        assertEquals("pressed", newState[0]);
    }

    @Test
    public void testNonExistentState() {
        // 创建测试状态
        StateApplier<TestView> normalState = target -> target.setText("Normal");

        // 注册状态
        wrapper.registerState("normal", normalState);

        // 尝试切换到不存在的状态
        wrapper.switchToState("non_existent");

        // 验证状态未改变
        assertEquals("normal", wrapper.getCurrentStateName());
        assertEquals("Normal", testView.getText());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullState() {
        // 尝试注册 null 状态
        wrapper.registerState("test", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullAlias() {
        // 创建测试状态
        StateApplier<TestView> normalState = target -> {};

        // 注册状态
        wrapper.registerState("normal", normalState);

        // 尝试添加 null 别名
        wrapper.addStateAlias(null, "normal");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOriginalStateName() {
        // 创建测试状态
        StateApplier<TestView> normalState = target -> {};

        // 注册状态
        wrapper.registerState("normal", normalState);

        // 尝试添加 null 原始状态名称
        wrapper.addStateAlias("alias", null);
    }
}
