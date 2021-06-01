package com.one.russell.f_app.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.DiffUtil
import com.one.russell.f_app.data.Community
import com.one.russell.f_app.R

class CommunityListAdapter(
    private val itemClickListener: OnItemClickListener,
    diffUtilCallback: DiffUtil.ItemCallback<Community>
) : PagedListAdapter<Community, CommunityListAdapter.ViewHolder>(diffUtilCallback) {

    interface OnItemClickListener {
        fun onItemClicked(item: Community)
        fun onItemLongClicked(item: Community)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_community, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val community: Community? = getItem(position)
        if (community != null) {
            holder.bind(community, itemClickListener)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var titleTextView: TextView = itemView.findViewById(R.id.txt_title)
        private var logoImageView: ImageView = itemView.findViewById(R.id.img_icon)
        private var selectionGroup: View = itemView.findViewById(R.id.selection_group)

        fun bind(community: Community, clickListener: OnItemClickListener) {
            titleTextView.text = community.title

            Glide
                .with(itemView)
                .load(community.logoUrl)
                .centerCrop()
                .placeholder(R.color.placeholder)
                .into(logoImageView)

            selectionGroup.visibility = if (community.isSelected) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                community.isSelected = !community.isSelected
                selectionGroup.visibility = if (community.isSelected) View.VISIBLE else View.GONE
                clickListener.onItemClicked(community)
            }

            itemView.setOnLongClickListener {
                clickListener.onItemLongClicked(community)
                return@setOnLongClickListener true
            }
        }
    }

    internal class CommunityDiffUtilCallback : DiffUtil.ItemCallback<Community>() {

        override fun areItemsTheSame(oldItem: Community, newItem: Community): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Community, newItem: Community) = oldItem == newItem
    }
}