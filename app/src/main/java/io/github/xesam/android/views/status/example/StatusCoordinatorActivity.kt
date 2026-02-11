package io.github.xesam.android.views.status.example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.github.xesam.android.views.status.StatusCoordinator
import io.github.xesam.android.views.status.example.databinding.ActivityStatusCoordinatorBinding

/**
 * StatusCoordinator 演示页面
 * 展示如何使用 StatusCoordinator 进行非侵入式的状态管理
 * 不需要替换现有组件，只是作为协调者管理现有视图
 */
class StatusCoordinatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatusCoordinatorBinding
    private lateinit var coordinator: StatusCoordinator
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusCoordinatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupStatusCoordinator()
        setupListeners()
        setupStatusChangeListener()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "StatusCoordinator 演示"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupStatusCoordinator() {
        // 创建并配置 StatusCoordinator
        coordinator = StatusCoordinator()

        // 注册状态和对应的视图
        coordinator
            .registerStatus("content", binding.contentView)
            .registerStatus("loading", binding.loadingView)
            .registerStatus("empty", binding.emptyView)
            .registerStatus("error", binding.errorView)

        // 初始状态
        binding.statusText.text = "状态: ${coordinator.currentStatus}"
        logRegisteredStatuses()
    }

    private fun setupListeners() {
        binding.apply {
            // 显示内容
            showContentButton.setOnClickListener {
                Log.d("StatusCoordinator", "Setting status to content")
                coordinator.setStatus("content")
                logRegisteredStatuses()
            }

            // 显示加载状态
            showLoadingButton.setOnClickListener {
                Log.d("StatusCoordinator", "Setting status to loading")
                coordinator.setStatus("loading")
                logRegisteredStatuses()
                // 模拟加载完成后自动切换到内容
                handler.postDelayed({
                    Log.d("StatusCoordinator", "Auto-switching to content after loading")
                    coordinator.setStatus("content")
                }, 2000)
            }

            // 显示空状态
            showEmptyButton.setOnClickListener {
                Log.d("StatusCoordinator", "Setting status to empty")
                coordinator.setStatus("empty")
                logRegisteredStatuses()
            }

            // 显示错误状态
            showErrorButton.setOnClickListener {
                Log.d("StatusCoordinator", "Setting status to error")
                coordinator.setStatus("error")
                logRegisteredStatuses()
            }

            // 重试按钮（在错误状态中）
            errorRetryButton.setOnClickListener {
                Log.d("StatusCoordinator", "Retry from error - setting status to loading")
                coordinator.setStatus("loading")
                // 模拟重试
                handler.postDelayed({
                    Log.d("StatusCoordinator", "Retry complete - switching to content")
                    coordinator.setStatus("content")
                }, 1500)
            }

            // 空状态重试按钮
            emptyRetryButton.setOnClickListener {
                Log.d("StatusCoordinator", "Retry from empty - setting status to loading")
                coordinator.setStatus("loading")
                // 模拟加载数据
                handler.postDelayed({
                    Log.d("StatusCoordinator", "Data loaded - switching to content")
                    coordinator.setStatus("content")
                }, 1500)
            }
        }
    }

    private fun setupStatusChangeListener() {
        coordinator.addOnStatusChangeListener {
                oldStatus, newStatus ->
            binding.statusText.text = "状态: $newStatus (从 $oldStatus 切换)"
        }

        // 添加状态未找到监听器
        coordinator.setOnStatusNotFoundListener {
                status ->
            Log.e("StatusCoordinator", "❌ 状态未找到: '$status'")
            android.widget.Toast.makeText(
                this,
                "状态 '$status' 未注册",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun logRegisteredStatuses() {
        val registeredStatuses = coordinator.getRegisteredStatuses()
        Log.d("StatusCoordinator", "Registered statuses: $registeredStatuses")
        Log.d("StatusCoordinator", "Current status: ${coordinator.getCurrentStatus()}")

        registeredStatuses.forEach { status ->
            val view = coordinator.getViewForStatus(status)
            Log.d(
                "StatusCoordinator",
                "Status '$status' view: ${view?.javaClass?.simpleName}, visibility: ${view?.visibility}"
            )
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
