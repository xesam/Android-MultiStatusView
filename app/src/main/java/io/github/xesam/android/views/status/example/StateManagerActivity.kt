package io.github.xesam.android.views.status.example

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.github.xesam.android.views.status.OnStatusChangeListener
import io.github.xesam.android.views.status.StateApplier
import io.github.xesam.android.views.status.StateManager
import io.github.xesam.android.views.status.example.databinding.ActivityStateManagerBinding

class StateManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStateManagerBinding
    private lateinit var stateManager: StateManager<State, Button>

    enum class State {
        ENABLED,
        DISABLED,
        LOADING,
        ERROR
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStateManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupStateManager()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "StateManager 演示"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupStateManager() {
        // 创建 StateManager，状态类型为 Enum，目标类型为 Button
        stateManager = StateManager(binding.targetButton)

        // 注册状态及其对应的 StateApplier
        stateManager
            .registerState(State.ENABLED, StateApplier { button ->
                button.isEnabled = true
                button.text = "可点击"
                button.alpha = 1.0f
            })
            .registerState(State.DISABLED, StateApplier { button ->
                button.isEnabled = false
                button.text = "已禁用"
                button.alpha = 0.5f
            })
            .registerState(State.LOADING, StateApplier { button ->
                button.isEnabled = false
                button.text = "加载中..."
                button.alpha = 0.7f
            })
            .registerState(State.ERROR, StateApplier { button ->
                button.isEnabled = false
                button.text = "出错了"
                button.alpha = 0.5f
            })
            // 添加状态别名
            .addStateAlias(State.LOADING, State.ENABLED) // 复用 ENABLED 的样式

        // 设置状态变化监听
        stateManager.setOnStatusChangeListener { oldState, newState ->
            val logText = "状态切换: ${oldState?.name} → ${newState?.name}"
            Log.d("StateManager", logText)
            binding.statusText.text = logText
        }

        // 初始状态
        binding.statusText.text = "当前状态: ${stateManager.currentStateName?.name}"

        setupListeners()
    }

    private fun setupListeners() {
        binding.apply {
            enableButton.setOnClickListener {
                stateManager.switchToState(State.ENABLED)
            }

            disableButton.setOnClickListener {
                stateManager.switchToState(State.DISABLED)
            }

            loadingButton.setOnClickListener {
                stateManager.switchToState(State.LOADING)
            }

            errorButton.setOnClickListener {
                stateManager.switchToState(State.ERROR)
            }

            getStatusButton.setOnClickListener {
                val currentState = stateManager.currentStateName
                val registeredStates = stateManager.registeredStates
                val info = "当前状态: ${currentState?.name}\n已注册: $registeredStates"
                android.widget.Toast.makeText(this@StateManagerActivity, info, android.widget.Toast.LENGTH_SHORT).show()
                Log.d("StateManager", info)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
