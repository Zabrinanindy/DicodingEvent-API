package com.aplikasi.dicodingevents.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.dicodingevents.R
import com.aplikasi.dicodingevents.data.response.ListEventsItem
import com.aplikasi.dicodingevents.databinding.ItemEventBinding
import com.bumptech.glide.Glide

class EventAdapter(
    private val events: MutableList<ListEventsItem>,
    private val itemClickListener: (ListEventsItem) -> Unit
) : RecyclerView.Adapter<EventAdapter.MyViewHolder>() {

    class MyViewHolder(
        private val binding: ItemEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: ListEventsItem, itemClickListener: (ListEventsItem) -> Unit) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(event.imageLogo)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(tvImageEvent)

                tvEventName.text = event.name
                tvEventOwner.text = event.ownerName
                tvEventTime.text = "${event.beginTime}"
                itemView.setOnClickListener {
                    itemClickListener(event)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event, itemClickListener)
    }

    override fun getItemCount(): Int = events.size

    fun submitList(newEvents: List<ListEventsItem>) {
        events.clear()
        events.addAll(newEvents)
        notifyDataSetChanged()
    }
}
