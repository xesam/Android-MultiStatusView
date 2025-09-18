package io.github.xesam.android.views.status.example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import io.github.xesam.android.views.status.example.databinding.ActivityResourceIdBinding

/**
 * 资源ID方式演示
 * 展示通过代码注册已存在视图的使用方式
 */
class ResourceIdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResourceIdBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResourceIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupMultiStatusView()
        setupListeners()
        setupStatusChangeListener()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "资源ID方式演示"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupMultiStatusView() {
        // 使用资源ID方式注册状态视图
        // 注意：这些视图必须在布局中存在，且ID正确
        binding.multiStatusView.apply {
            registerStatusByViewId("content", R.id.contentView)
            registerStatusByViewId("loading", R.id.loadingView)
            registerStatusByViewId("empty", R.id.emptyView)
            registerStatusByViewId("error", R.id.errorView)
        }
        binding.statusText.text = "状态: ${binding.multiStatusView.currentStatus}"
    }

    private fun setupListeners() {
        binding.apply {
            // 显示内容
            showContentButton.setOnClickListener {
                multiStatusView.setStatus("content")
            }

            // 显示加载状态
            showLoadingButton.setOnClickListener {
                multiStatusView.setStatus("loading")
                // 模拟加载完成后自动切换到内容
                handler.postDelayed({
                    multiStatusView.setStatus("content")
                }, 2000)
            }

            // 显示空状态
            showEmptyButton.setOnClickListener {
                multiStatusView.setStatus("empty")
            }

            // 显示错误状态
            showErrorButton.setOnClickListener {
                multiStatusView.setStatus("error")
            }

            // 重试按钮（在错误状态中）
            retryErrorButton.setOnClickListener {
                multiStatusView.setStatus("loading")
                // 模拟重试
                handler.postDelayed({
                    multiStatusView.setStatus("content")
                }, 1500)
            }

            // 空状态重试按钮
            retryEmptyButton.setOnClickListener {
                multiStatusView.setStatus("loading")
                // 模拟加载数据
                handler.postDelayed({
                    multiStatusView.setStatus("content")
                }, 1500)
            }
        }
    }

    private fun setupStatusChangeListener() {
        binding.multiStatusView.addOnStatusChangeListener { oldStatus, newStatus ->
            binding.statusText.text = "状态: $newStatus (从 $oldStatus 切换)"
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