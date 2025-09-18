package io.github.xesam.android.views.status.example

/**
 * 演示项目数据模型
 */
data class DemoItem(
    val id: Int,
    val title: String,
    val description: String,
    val activityClass: Class<*>,
    val icon: String = ""
) {
    companion object {
        /**
         * 获取所有演示项目
         */
        fun getAllDemos(): List<DemoItem> {
            return listOf(
                DemoItem(
                    id = 1,
                    title = "XML内嵌方式",
                    description = "通过XML布局文件内嵌状态视图的方式",
                    activityClass = XmlEmbeddedActivity::class.java,
                    icon = "📄"
                ),
                DemoItem(
                    id = 2,
                    title = "资源ID方式",
                    description = "通过视图ID注册状态视图的方式",
                    activityClass = ResourceIdActivity::class.java,
                    icon = "🔑"
                ),
                DemoItem(
                    id = 3,
                    title = "布局资源方式",
                    description = "通过布局资源文件注册状态视图的方式",
                    activityClass = LayoutResourceActivity::class.java,
                    icon = "🎯"
                ),
                DemoItem(
                    id = 4,
                    title = "混合配置方式",
                    description = "XML自动发现 + 代码注册状态的混合方式",
                    activityClass = MixedConfigActivity::class.java,
                    icon = "⚙️"
                ),
                DemoItem(
                    id = 5,
                    title = "高级特性演示",
                    description = "状态别名、监听器、错误处理等高级功能",
                    activityClass = AdvancedFeaturesActivity::class.java,
                    icon = "🚀"
                )
            )
        }
        
        /**
         * 根据ID获取演示项目
         */
        fun getDemoById(id: Int): DemoItem? {
            return getAllDemos().find { it.id == id }
        }
    }
}