package io.github.xesam.android.views.status.example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import io.github.xesam.android.views.status.example.databinding.ActivityXmlEmbeddedBinding

/**
 * XML内嵌方式演示
 * 展示在XML中直接声明状态子组件的使用方式
 */
class XmlEmbeddedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityXmlEmbeddedBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityXmlEmbeddedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
        setupStatusChangeListener()

        // 延迟检查状态注册情况，确保视图初始化完成
        handler.postDelayed({
            logRegisteredStatuses()
        }, 500)
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "XML内嵌方式演示"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupListeners() {
        binding.apply {
            // 显示内容
            showContentButton.setOnClickListener {
                android.util.Log.d("XmlEmbeddedActivity", "Setting status to content")
                multiStatusView.setStatus("content")
                logRegisteredStatuses()
            }

            // 显示加载状态
            showLoadingButton.setOnClickListener {
                android.util.Log.d("XmlEmbeddedActivity", "Setting status to loading")
                multiStatusView.setStatus("loading")
                logRegisteredStatuses()
                // 模拟加载完成后自动切换到内容
                handler.postDelayed({
                    android.util.Log.d(
                        "XmlEmbeddedActivity",
                        "Auto-switching to content after loading"
                    )
                    multiStatusView.setStatus("content")
                }, 2000)
            }

            // 显示空状态
            showEmptyButton.setOnClickListener {
                android.util.Log.d("XmlEmbeddedActivity", "Setting status to empty")
                multiStatusView.setStatus("empty")
                logRegisteredStatuses()
            }

            // 显示错误状态
            showErrorButton.setOnClickListener {
                android.util.Log.d("XmlEmbeddedActivity", "Setting status to error")
                multiStatusView.setStatus("error")
                logRegisteredStatuses()
            }

            // 重试按钮（在错误状态中）
            errorRetryButton.setOnClickListener {
                android.util.Log.d(
                    "XmlEmbeddedActivity",
                    "Retry from error - setting status to loading"
                )
                multiStatusView.setStatus("loading")
                // 模拟重试
                handler.postDelayed({
                    android.util.Log.d(
                        "XmlEmbeddedActivity",
                        "Retry complete - switching to content"
                    )
                    multiStatusView.setStatus("content")
                }, 1500)
            }

            // 空状态重试按钮
            emptyRetryButton.setOnClickListener {
                android.util.Log.d(
                    "XmlEmbeddedActivity",
                    "Retry from empty - setting status to loading"
                )
                multiStatusView.setStatus("loading")
                // 模拟加载数据
                handler.postDelayed({
                    android.util.Log.d("XmlEmbeddedActivity", "Data loaded - switching to content")
                    multiStatusView.setStatus("content")
                }, 1500)
            }
        }
        binding.statusText.text = "状态: ${binding.multiStatusView.currentStatus}"
    }

    private fun logRegisteredStatuses() {
        val registeredStatuses = binding.multiStatusView.getRegisteredStatuses()
        android.util.Log.d("XmlEmbeddedActivity", "Registered statuses: $registeredStatuses")
        android.util.Log.d(
            "XmlEmbeddedActivity",
            "Current status: ${binding.multiStatusView.getCurrentStatus()}"
        )

        registeredStatuses.forEach { status ->
            val view = binding.multiStatusView.getViewForStatus(status)
            android.util.Log.d(
                "XmlEmbeddedActivity",
                "Status '$status' view: ${view?.javaClass?.simpleName}, visibility: ${view?.visibility}"
            )
        }
    }

    private fun setupStatusChangeListener() {
        binding.multiStatusView.addOnStatusChangeListener { oldStatus, newStatus ->
            binding.statusText.text = "状态: $newStatus (从 $oldStatus 切换)"
        }

        // 添加状态未找到监听器
        binding.multiStatusView.setOnStatusNotFoundListener { status ->
            android.util.Log.e("XmlEmbeddedActivity", "❌ 状态未找到: '$status'")
            android.widget.Toast.makeText(
                this,
                "状态 '$status' 未注册",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}