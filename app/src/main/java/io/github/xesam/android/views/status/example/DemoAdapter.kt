package io.github.xesam.android.views.status.example

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.xesam.android.views.status.example.databinding.ItemDemoBinding

/**
 * 演示列表适配器
 */
class DemoAdapter(
    private val onItemClick: (MainActivity.DemoItem) -> Unit
) : ListAdapter<MainActivity.DemoItem, DemoAdapter.DemoViewHolder>(DemoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoViewHolder {
        val binding = ItemDemoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DemoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DemoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DemoViewHolder(
        private val binding: ItemDemoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MainActivity.DemoItem) {
            binding.apply {
                titleText.text = item.title
                descriptionText.text = item.description
                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }

    class DemoDiffCallback : DiffUtil.ItemCallback<MainActivity.DemoItem>() {
        override fun areItemsTheSame(
            oldItem: MainActivity.DemoItem,
            newItem: MainActivity.DemoItem
        ): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: MainActivity.DemoItem,
            newItem: MainActivity.DemoItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}