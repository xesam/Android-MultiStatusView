package io.github.xesam.android.views.status;

import android.graphics.Color;
import android.widget.Button;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

/**
 * StateViewWrapper 单元测试
 */
public class StateManagerTest {

    @Mock
    private Button mockButton;

    @Mock
    private TextView mockTextView;

    private StateManager<String, Button> buttonWrapper;
    private StateManager<String, TextView> textViewWrapper;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        buttonWrapper = new StateManager<>(mockButton);
        textViewWrapper = new StateManager<>(mockTextView);
    }

    @Test
    public void testRegisterState() {
        // 创建测试状态
        StateApplier<Button> normalState = target -> target.setText("Normal");

        // 注册状态
        buttonWrapper.registerState("normal", normalState);

        // 验证状态已注册
        assertTrue(buttonWrapper.getRegisteredStates().contains("normal"));
        assertEquals("normal", buttonWrapper.getCurrentStateName());
    }

    @Test
    public void testSwitchState() {
        // 创建测试状态
        StateApplier<Button> normalState = target -> target.setText("Normal");
        StateApplier<Button> pressedState = target -> target.setText("Pressed");

        // 注册状态
        buttonWrapper.registerState("normal", normalState).registerState("pressed", pressedState);

        // 切换状态
        buttonWrapper.switchToState("pressed");

        // 验证状态已切换
        assertEquals("pressed", buttonWrapper.getCurrentStateName());
        verify(mockButton).setText("Pressed");
    }

    @Test
    public void testStateAlias() {
        // 创建测试状态
        StateApplier<Button> pressedState = target -> target.setText("Pressed");

        // 注册状态并添加别名
        buttonWrapper.registerState("pressed", pressedState).addStateAlias("active", "pressed");

        // 重置mock以忽略注册时的自动调用
        org.mockito.Mockito.reset(mockButton);

        // 使用别名切换状态
        buttonWrapper.switchToState("active");

        // 验证状态已切换
        assertEquals("pressed", buttonWrapper.getCurrentStateName());
        verify(mockButton).setText("Pressed");
    }

    @Test
    public void testStateChangedListener() {
        // 创建测试状态
        StateApplier<Button> normalState = target -> target.setText("Normal");
        StateApplier<Button> pressedState = target -> target.setText("Pressed");

        // 注册状态
        buttonWrapper.registerState("normal", normalState).registerState("pressed", pressedState);

        // 记录监听器调用
        final String[] oldState = new String[1];
        final String[] newState = new String[1];

        // 设置监听器
        buttonWrapper.setOnStatusChangeListener((oldStateName, newStateName) -> {
            oldState[0] = oldStateName;
            newState[0] = newStateName;
        });

        // 切换状态
        buttonWrapper.switchToState("pressed");

        // 验证监听器被调用
        assertEquals("normal", oldState[0]);
        assertEquals("pressed", newState[0]);
    }

    @Test
    public void testNonExistentState() {
        // 创建测试状态
        StateApplier<Button> normalState = target -> target.setText("Normal");

        // 注册状态
        buttonWrapper.registerState("normal", normalState);

        // 尝试切换到不存在的状态
        buttonWrapper.switchToState("non_existent");

        // 验证状态未改变
        assertEquals("normal", buttonWrapper.getCurrentStateName());
    }

    @Test
    public void testGenericTypeSupport() {
        // 创建 TextView 状态
        StateApplier<TextView> stateA = target -> {
            target.setText("State A");
            target.setTextColor(Color.RED);
        };

        StateApplier<TextView> stateB = target -> {
            target.setText("State B");
            target.setTextColor(Color.BLUE);
        };

        // 注册状态并切换
        textViewWrapper.registerState("stateA", stateA).registerState("stateB", stateB);
        textViewWrapper.switchToState("stateB");

        // 验证状态已切换
        assertEquals("stateB", textViewWrapper.getCurrentStateName());
        verify(mockTextView).setText("State B");
        verify(mockTextView).setTextColor(Color.BLUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullState() {
        // 尝试注册 null 状态
        buttonWrapper.registerState("test", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullAlias() {
        // 创建测试状态
        StateApplier<Button> normalState = target -> {};

        // 注册状态
        buttonWrapper.registerState("normal", normalState);

        // 尝试添加 null 别名
        buttonWrapper.addStateAlias(null, "normal");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOriginalStateName() {
        // 创建测试状态
        StateApplier<Button> normalState = target -> {};

        // 注册状态
        buttonWrapper.registerState("normal", normalState);

        // 尝试添加 null 原始状态名称
        buttonWrapper.addStateAlias("alias", null);
    }
}
