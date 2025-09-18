package io.github.xesam.android.views.status.example

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * RelativeLayout版本的MultiStatusView演示
 * 展示如何使用基于RelativeLayout的RelativeMultiStatusView
 * 
 * 功能与XmlEmbeddedActivity完全相同，只是使用了不同的容器布局
 */
class RelativeLayoutActivity : AppCompatActivity() {

    private lateinit var multiStatusView: RelativeMultiStatusView
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relative_layout)

        initViews()
        setupListeners()
        setupStatusChangeListener()
        
        // 初始状态为loading
        multiStatusView.setStatus("loading")
        updateStatusText("loading")
    }

    private fun initViews() {
        multiStatusView = findViewById(R.id.relative_multi_status_view)
        statusText = findViewById(R.id.status_text)
    }

    private fun setupListeners() {
        findViewById<Button>(R.id.btn_show_content).setOnClickListener {
            multiStatusView.setStatus("content")
            updateStatusText("content")
        }

        findViewById<Button>(R.id.btn_show_loading).setOnClickListener {
            multiStatusView.setStatus("loading")
            updateStatusText("loading")
        }

        findViewById<Button>(R.id.btn_show_empty).setOnClickListener {
            multiStatusView.setStatus("empty")
            updateStatusText("empty")
        }

        findViewById<Button>(R.id.btn_show_error).setOnClickListener {
            multiStatusView.setStatus("error")
            updateStatusText("error")
        }

        // 演示状态未找到的情况
        findViewById<Button>(R.id.btn_show_unknown).setOnClickListener {
            multiStatusView.setStatus("unknown_status")
            updateStatusText("unknown_status (should trigger onStatusNotFound)")
        }

        // 演示自动切换状态（模拟网络请求）
        findViewById<Button>(R.id.btn_auto_switch).setOnClickListener {
            simulateNetworkRequest()
        }
    }

    private fun setupStatusChangeListener() {
        // 添加状态变化监听器
        multiStatusView.addOnStatusChangeListener { oldStatus, newStatus ->
            DemoUtils.showToast(this, "状态变化: $oldStatus -> $newStatus")
        }

        // 设置状态未找到监听器
        multiStatusView.setOnStatusNotFoundListener { status ->
            DemoUtils.showToast(this, "状态未找到: $status")
        }

        // 设置错误处理器
        multiStatusView.setErrorHandler { exception ->
            DemoUtils.showToast(this, "错误: ${exception.message}")
        }
    }

    private fun updateStatusText(status: String) {
        statusText.text = "当前状态: $status"
    }

    private fun simulateNetworkRequest() {
        // 开始加载
        multiStatusView.setStatus("loading")
        updateStatusText("loading")

        // 模拟网络请求延迟
        statusText.postDelayed({
            // 随机决定返回内容还是空状态
            val hasData = Math.random() > 0.5
            if (hasData) {
                multiStatusView.setStatus("content")
                updateStatusText("content (auto switched)")
            } else {
                multiStatusView.setStatus("empty")
                updateStatusText("empty (auto switched)")
            }
        }, 2000)
    }
}