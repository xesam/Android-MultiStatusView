package io.github.xesam.android.views.status.example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.github.xesam.android.views.status.example.databinding.ActivityMixedConfigBinding

/**
 * 混合配置方式演示
 * 展示多种配置方式混合使用的高级用法
 */
class MixedConfigActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMixedConfigBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMixedConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupMixedConfiguration()
        setupListeners()
        setupAdvancedFeatures()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "混合配置方式演示"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupMixedConfiguration() {
        // 混合配置：XML自动发现 + 代码注册
        binding.multiStatusView.apply {
            // XML中的状态会被自动发现（content, loading, empty, error）
            // 额外添加自定义状态
            registerStatusByViewId("error", R.id.id_error)
            registerStatusByLayout("custom_loading", R.layout.layout_custom_loading)
            registerStatusByLayout("network_error", R.layout.layout_network_error) { view ->
                view.findViewById<Button>(R.id.networkRetryButton)?.setOnClickListener {
                    simulateNetworkRequest()
                }
            }
            
            // 添加状态别名
            addStatusAlias("net_error", "network_error")
            
            // 设置默认状态
            setStatus("content")
        }
    }

    private fun setupListeners() {
        binding.apply {
            // 基本状态切换
            showContentButton.setOnClickListener {
                multiStatusView.setStatus("content")
            }

            showLoadingButton.setOnClickListener {
                simulateDataLoading()
            }

            showEmptyButton.setOnClickListener {
                multiStatusView.setStatus("empty")
            }

            showErrorButton.setOnClickListener {
                multiStatusView.setStatus("error")
            }

            // 自定义状态
            showCustomLoadingButton.setOnClickListener {
                multiStatusView.setStatus("custom_loading")
            }

            showNetworkErrorButton.setOnClickListener {
                multiStatusView.setStatus("network_error")
            }

            // 使用别名
            showNetworkErrorAliasButton.setOnClickListener {
                multiStatusView.setStatus("net_error")
            }
        }
    }

    private fun setupAdvancedFeatures() {
        binding.multiStatusView.apply {
            // 状态变化监听
            addOnStatusChangeListener { oldStatus, newStatus ->
                binding.statusText.text = "状态: $newStatus (从 $oldStatus 切换)"
                Toast.makeText(
                    this@MixedConfigActivity, 
                    "状态切换到: $newStatus", 
                    Toast.LENGTH_SHORT
                ).show()
            }

            // 状态未找到监听
            setOnStatusNotFoundListener { status ->
                Toast.makeText(
                    this@MixedConfigActivity,
                    "未找到状态: $status",
                    Toast.LENGTH_LONG
                ).show()
            }

            // 错误处理
            setErrorHandler { exception ->
                Toast.makeText(
                    this@MixedConfigActivity,
                    "错误: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // 显示当前注册的状态
        binding.showRegisteredStatusesButton.setOnClickListener {
            val statuses = binding.multiStatusView.getRegisteredStatuses()
            Toast.makeText(
                this,
                "已注册状态: ${statuses.joinToString(", ")}",
                Toast.LENGTH_LONG
            ).show()
        }

        // 动态注册新状态
        binding.registerNewStatusButton.setOnClickListener {
            binding.multiStatusView.registerStatusByLayout(
                "dynamic_status",
                R.layout.layout_dynamic
            ) { view ->
                view.findViewById<Button>(R.id.dynamicButton)?.setOnClickListener {
                    Toast.makeText(this, "动态状态按钮点击", Toast.LENGTH_SHORT).show()
                    binding.multiStatusView.setStatus("content")
                }
            }
            
            Toast.makeText(this, "已注册动态状态", Toast.LENGTH_SHORT).show()
        }

        // 切换到动态注册的状态
        binding.showDynamicStatusButton.setOnClickListener {
            binding.multiStatusView.setStatus("dynamic_status")
        }
    }

    private fun simulateDataLoading() {
        binding.multiStatusView.setStatus("loading")
        
        // 模拟数据加载
        handler.postDelayed({
            // 随机决定结果
            val random = (1..4).random()
            when (random) {
                1 -> binding.multiStatusView.setStatus("content")
                2 -> binding.multiStatusView.setStatus("empty")
                3 -> binding.multiStatusView.setStatus("error")
                4 -> binding.multiStatusView.setStatus("network_error")
            }
        }, 2000)
    }

    private fun simulateNetworkRequest() {
        binding.multiStatusView.setStatus("custom_loading")
        
        // 模拟网络请求
        handler.postDelayed({
            // 随机决定结果
            val random = (1..3).random()
            when (random) {
                1 -> binding.multiStatusView.setStatus("content")
                2 -> binding.multiStatusView.setStatus("network_error")
                3 -> binding.multiStatusView.setStatus("empty")
            }
        }, 2500)
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