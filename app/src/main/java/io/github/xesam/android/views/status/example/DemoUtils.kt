package io.github.xesam.android.views.status.example

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import io.github.xesam.android.views.status.MultiStatusView
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 演示工具类
 * 提供常用的工具方法和扩展函数
 */
object DemoUtils {
    
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    
    /**
     * 显示Toast消息
     */
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
    
    /**
     * 获取当前时间字符串
     */
    fun getCurrentTimeString(): String {
        return dateFormat.format(Date())
    }
    
    /**
     * 格式化时间间隔
     */
    fun formatDuration(durationMs: Long): String {
        return when {
            durationMs < 1000 -> "${durationMs}ms"
            durationMs < 60000 -> "${durationMs / 1000}s ${durationMs % 1000}ms"
            else -> "${durationMs / 60000}m ${(durationMs % 60000) / 1000}s"
        }
    }
    
    /**
     * 获取状态颜色
     */
    fun getStatusColor(context: Context, status: String): Int {
        return when (status) {
            "content" -> ContextCompat.getColor(context, R.color.status_content)
            "loading" -> ContextCompat.getColor(context, R.color.status_loading)
            "empty" -> ContextCompat.getColor(context, R.color.status_empty)
            "error" -> ContextCompat.getColor(context, R.color.status_error)
            else -> ContextCompat.getColor(context, R.color.text_secondary)
        }
    }
    
    /**
     * 延迟执行
     */
    fun delay(millis: Long, action: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(millis)
            action()
        }
    }
}