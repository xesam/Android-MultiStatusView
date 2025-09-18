package io.github.xesam.android.views.status.example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.github.xesam.android.views.status.example.databinding.ActivityLayoutResourceBinding

/**
 * 布局资源方式演示
 * 展示通过代码注册布局资源的使用方式
 */
class LayoutResourceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLayoutResourceBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLayoutResourceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupMultiStatusView()
        setupListeners()
        setupStatusChangeListener()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "布局资源方式演示"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupMultiStatusView() {
        // 使用布局资源方式注册状态视图
        binding.multiStatusView.apply {
            // 注册内容状态（使用布局资源）
            registerStatusByLayout("content", R.layout.layout_content) { view ->
                // 初始化内容视图
                view.findViewById<Button>(R.id.refreshButton)?.setOnClickListener {
                    refreshData()
                }
            }

            // 注册加载状态（使用布局资源）
            registerStatusByLayout("loading", R.layout.layout_loading)

            // 注册空状态（使用布局资源）
            registerStatusByLayout("empty", R.layout.layout_empty) { view ->
                // 初始化空状态视图
                view.findViewById<Button>(R.id.emptyRetryButton)?.setOnClickListener {
                    loadData()
                }
            }

            // 注册错误状态（使用布局资源）
            registerStatusByLayout("error", R.layout.layout_error) { view ->
                // 初始化错误状态视图
                view.findViewById<Button>(R.id.errorRetryButton)?.setOnClickListener {
                    loadData()
                }
            }
            binding.statusText.text = "状态: ${binding.multiStatusView.currentStatus}"
        }
    }

    private fun setupListeners() {
        binding.apply {
            // 显示内容
            showContentButton.setOnClickListener {
                multiStatusView.setStatus("content")
            }

            // 显示加载状态
            showLoadingButton.setOnClickListener {
                loadData()
            }

            // 显示空状态
            showEmptyButton.setOnClickListener {
                multiStatusView.setStatus("empty")
            }

            // 显示错误状态
            showErrorButton.setOnClickListener {
                multiStatusView.setStatus("error")
            }
        }
    }

    private fun setupStatusChangeListener() {
        binding.multiStatusView.addOnStatusChangeListener { oldStatus, newStatus ->
            binding.statusText.text = "状态: $newStatus (从 $oldStatus 切换)"

            // 在状态变化时显示提示
            Toast.makeText(this, "状态切换到: $newStatus", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadData() {
        binding.multiStatusView.setStatus("loading")

        // 模拟网络请求
        handler.postDelayed({
            // 随机决定结果
            val random = (1..3).random()
            when (random) {
                1 -> binding.multiStatusView.setStatus("content")
                2 -> binding.multiStatusView.setStatus("empty")
                3 -> binding.multiStatusView.setStatus("error")
            }
        }, 2000)
    }

    private fun refreshData() {
        Toast.makeText(this, "刷新数据...", Toast.LENGTH_SHORT).show()
        loadData()
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