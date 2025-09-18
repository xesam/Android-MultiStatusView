package io.github.xesam.android.views.status.example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.github.xesam.android.views.status.example.databinding.ActivityAdvancedFeaturesBinding

/**
 * 高级特性演示Activity
 * 展示状态别名、监听器、错误处理等高级功能
 */
class AdvancedFeaturesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdvancedFeaturesBinding
    private val handler = Handler(Looper.getMainLooper())
    private var statusChangeCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvancedFeaturesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupAdvancedFeatures()
        setupErrorHandling()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "高级特性演示"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupAdvancedFeatures() {
        binding.multiStatusView.apply {
            // 注册状态
            registerStatusByLayout("content", R.layout.layout_content_advanced)
            registerStatusByLayout("loading", R.layout.layout_loading_advanced)
            registerStatusByLayout("empty", R.layout.layout_empty_advanced)
            registerStatusByLayout("error", R.layout.layout_error_advanced)
            
            // 添加状态别名
            addStatusAlias("loading_alias", "loading")
            addStatusAlias("error_alias", "error")
            addStatusAlias("content_alias", "content")
        }

        // 状态变化监听
        binding.multiStatusView.addOnStatusChangeListener { oldStatus, newStatus ->
            statusChangeCount++
            binding.statusChangeCount.text = "状态变化次数: $statusChangeCount"
            binding.statusText.text = "状态: $newStatus (从 $oldStatus 切换)"
            
            // 记录状态变化历史
            val history = binding.statusHistory.text.toString()
            val newHistory = if (history.isEmpty()) {
                "$oldStatus → $newStatus"
            } else {
                "$history\n$oldStatus → $newStatus"
            }
            binding.statusHistory.text = newHistory
            
            // 自动滚动到底部
            binding.statusHistoryScrollView.post {
                binding.statusHistoryScrollView.fullScroll(android.view.View.FOCUS_DOWN)
            }
        }

        // 使用别名切换状态
        binding.useAliasButton.setOnClickListener {
            val aliases = listOf("loading_alias", "error_alias", "content_alias")
            val randomAlias = aliases.random()
            binding.multiStatusView.setStatus(randomAlias)
            Toast.makeText(this, "使用别名: $randomAlias", Toast.LENGTH_SHORT).show()
        }

        // 基本状态切换按钮
        binding.showContentButton.setOnClickListener {
            binding.multiStatusView.setStatus("content")
        }

        binding.showLoadingButton.setOnClickListener {
            binding.multiStatusView.setStatus("loading")
        }

        binding.showEmptyButton.setOnClickListener {
            binding.multiStatusView.setStatus("empty")
        }

        binding.showErrorButton.setOnClickListener {
            binding.multiStatusView.setStatus("error")
        }
    }

    private fun setupErrorHandling() {
        // 测试状态未找到
        binding.testStatusNotFoundButton.setOnClickListener {
            binding.multiStatusView.setStatus("non_existent_status")
        }

        // 设置状态未找到监听
        binding.multiStatusView.setOnStatusNotFoundListener { status ->
            Toast.makeText(
                this,
                "⚠️ 状态未找到: $status",
                Toast.LENGTH_LONG
            ).show()
        }

        // 设置错误处理
        binding.multiStatusView.setErrorHandler { exception ->
            Toast.makeText(
                this,
                "❌ 错误: ${exception.message}",
                Toast.LENGTH_LONG
            ).show()
        }

        // 重置状态计数
        binding.resetCountButton.setOnClickListener {
            statusChangeCount = 0
            binding.statusChangeCount.text = "状态变化次数: 0"
            binding.statusHistory.text = ""
            Toast.makeText(this, "状态计数已重置", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}