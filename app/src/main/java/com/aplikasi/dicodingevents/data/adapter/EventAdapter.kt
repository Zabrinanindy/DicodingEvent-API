package com.aplikasi.dicodingevents.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.dicodingevents.R
import com.aplikasi.dicodingevents.data.local.EventEntity
import com.aplikasi.dicodingevents.databinding.ItemEventBinding
import com.bumptech.glide.Glide

class EventAdapter(
    private val onFavoriteClick: (EventEntity) -> Unit,
    private val onItemClick: (EventEntity) -> Unit
) : ListAdapter<EventEntity, EventAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event, onFavoriteClick, onItemClick)
    }

    class MyViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            event: EventEntity,
            onFavoriteClick: (EventEntity) -> Unit,
            onItemClick: (EventEntity) -> Unit
        ) {
            binding.apply {
                tvEventName.text = event.name
                tvEventOwner.text = event.ownerName
                tvEventTime.text = event.beginTime

                Glide.with(itemView.context)
                    .load(event.imageLogo)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(tvImageEvent)

                tvFavorite.setImageResource(
                    if (event.isFavorited) R.drawable.baseline_favorite_24
                    else R.drawable.baseline_favorite_border_24
                )

                tvFavorite.setOnClickListener { onFavoriteClick(event) }
                root.setOnClickListener { onItemClick(event) }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventEntity>() {
            override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}
