package io.github.xesam.android.views.status.example

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import io.github.xesam.android.views.status.MultiStatusView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/**
 * 演示应用扩展函数
 */

/**
 * 启动Activity的扩展函数
 */
inline fun <reified T : Activity> Context.startActivity(block: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.block()
    startActivity(intent)
}

/**
 * 隐藏软键盘
 */
fun Context.hideKeyboard(view: View? = null) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentView = view ?: (this as? Activity)?.currentFocus
    currentView?.let {
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

/**
 * 显示软键盘
 */
fun Context.showKeyboard(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    view.requestFocus()
    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

/**
 * 设置TextView文本和颜色
 */
fun TextView.setTextAndColor(text: String, colorRes: Int) {
    this.text = text
    setTextColor(ContextCompat.getColor(context, colorRes))
}

/**
 * 获取MultiStatusView的当前状态
 */
fun MultiStatusView.getCurrentStatusName(): String {
    return when (getCurrentStatus()) {
        "content" -> "内容状态"
        "loading" -> "加载状态"
        "empty" -> "空状态"
        "error" -> "错误状态"
        else -> "未知状态"
    }
}



/**
 * 获取状态视图
 */
fun MultiStatusView.getStatusView(status: String): View? {
    return when (status) {
        "content" -> getContentView()
        "loading" -> getLoadingView()
        "empty" -> getEmptyView()
        "error" -> getErrorView()
        else -> null
    }
}

/**
 * 获取内容视图
 */
fun MultiStatusView.getContentView(): View? {
    return getStatusView("content")
}

/**
 * 获取加载视图
 */
fun MultiStatusView.getLoadingView(): View? {
    return getStatusView("loading")
}

/**
 * 获取空视图
 */
fun MultiStatusView.getEmptyView(): View? {
    return getStatusView("empty")
}

/**
 * 获取错误视图
 */
fun MultiStatusView.getErrorView(): View? {
    return getStatusView("error")
}

/**
 * 视图可见性扩展
 */
fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE
fun View.isGone(): Boolean = visibility == View.GONE
fun View.isInvisible(): Boolean = visibility == View.INVISIBLE