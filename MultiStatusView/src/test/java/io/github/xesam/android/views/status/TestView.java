package io.github.xesam.android.views.status;

import android.view.View;

/**
 * 测试用的简单视图类
 * 用于避免在测试环境中调用 Android 视图方法的问题
 */
public class TestView extends View {

    private String text;
    private int textColor;
    private int backgroundColor;
    private float textSize;
    private boolean enabled;

    public TestView() {
        super(null);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
