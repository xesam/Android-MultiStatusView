package io.github.xesam.android.views.status.example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.github.xesam.android.views.status.StatusViewManager
import io.github.xesam.android.views.status.example.databinding.ActivityStatusViewManagerBinding

class StatusViewManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatusViewManagerBinding
    private lateinit var manager: StatusViewManager<String>
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusViewManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupStatusViewManager()
        setupListeners()
        setupStatusChangeListener()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "StatusViewManager 演示"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupStatusViewManager() {
        manager = StatusViewManager()

        manager
            .registerStatus("content", binding.contentView)
            .registerStatus("loading", binding.loadingView)
            .registerStatus("empty", binding.emptyView)
            .registerStatus("error", binding.errorView)

        binding.statusText.text = "状态: ${manager.currentStatus}"
        logRegisteredStatuses()
    }

    private fun setupListeners() {
        binding.apply {
            showContentButton.setOnClickListener {
                Log.d("StatusViewManager", "Setting status to content")
                manager.setStatus("content")
                logRegisteredStatuses()
            }

            showLoadingButton.setOnClickListener {
                Log.d("StatusViewManager", "Setting status to loading")
                manager.setStatus("loading")
                logRegisteredStatuses()
                handler.postDelayed({
                    Log.d("StatusViewManager", "Auto-switching to content after loading")
                    manager.setStatus("content")
                }, 2000)
            }

            showEmptyButton.setOnClickListener {
                Log.d("StatusViewManager", "Setting status to empty")
                manager.setStatus("empty")
                logRegisteredStatuses()
            }

            showErrorButton.setOnClickListener {
                Log.d("StatusViewManager", "Setting status to error")
                manager.setStatus("error")
                logRegisteredStatuses()
            }

            errorRetryButton.setOnClickListener {
                Log.d("StatusViewManager", "Retry from error - setting status to loading")
                manager.setStatus("loading")
                handler.postDelayed({
                    Log.d("StatusViewManager", "Retry complete - switching to content")
                    manager.setStatus("content")
                }, 1500)
            }

            emptyRetryButton.setOnClickListener {
                Log.d("StatusViewManager", "Retry from empty - setting status to loading")
                manager.setStatus("loading")
                handler.postDelayed({
                    Log.d("StatusViewManager", "Data loaded - switching to content")
                    manager.setStatus("content")
                }, 1500)
            }
        }
    }

    private fun setupStatusChangeListener() {
        manager.addOnStatusChangeListener { oldStatus, newStatus ->
            binding.statusText.text = "状态: $newStatus (从 $oldStatus 切换)"
        }

        manager.setOnStatusNotFoundListener { status ->
            Log.e("StatusViewManager", "❌ 状态未找到: '$status'")
            android.widget.Toast.makeText(
                this,
                "状态 '$status' 未注册",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun logRegisteredStatuses() {
        val registeredStatuses = manager.getRegisteredStatuses()
        Log.d("StatusViewManager", "Registered statuses: $registeredStatuses")
        Log.d("StatusViewManager", "Current status: ${manager.getCurrentStatus()}")

        registeredStatuses.forEach { status ->
            val view = manager.getViewForStatus(status)
            Log.d(
                "StatusViewManager",
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
