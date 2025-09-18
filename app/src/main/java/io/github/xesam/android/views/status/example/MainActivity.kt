package io.github.xesam.android.views.status.example

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.xesam.android.views.status.example.databinding.ActivityMainBinding

/**
 * 主活动 - MultiStatusView演示应用入口
 * 展示所有演示页面的入口
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: DemoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v: View?, insets: WindowInsetsCompat? ->
            val systemBars = insets!!.getInsets(WindowInsetsCompat.Type.systemBars())
            v!!.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupRecyclerView()
        setupDemoData()
    }

    private fun setupRecyclerView() {
        adapter = DemoAdapter { demoItem ->
            startActivity(Intent(this, demoItem.activityClass))
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupDemoData() {
        val demoItems = listOf(
            DemoItem(
                "XML内嵌方式演示",
                "展示在XML中直接声明状态子组件的使用方式",
                XmlEmbeddedActivity::class.java
            ),
            DemoItem(
                "资源ID方式演示",
                "展示通过代码注册已存在视图的使用方式",
                ResourceIdActivity::class.java
            ),
            DemoItem(
                "布局资源方式演示",
                "展示通过代码注册布局资源的使用方式",
                LayoutResourceActivity::class.java
            ),
            DemoItem(
                "混合配置方式演示",
                "展示多种配置方式混合使用的高级用法",
                MixedConfigActivity::class.java
            ),
            DemoItem(
                "RelativeLayout版本演示",
                "展示基于RelativeLayout实现的MultiStatusView",
                RelativeLayoutActivity::class.java
            ),
            DemoItem(
                "高级功能演示",
                "展示监听器、错误处理等高级功能",
                AdvancedFeaturesActivity::class.java
            )
        )

        adapter.submitList(demoItems)
    }

    data class DemoItem(
        val title: String,
        val description: String,
        val activityClass: Class<*>
    )
}