package com.kubsu.cubehub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kubsu.cubehub.R
import com.kubsu.cubehub.data.model.Group
import com.kubsu.cubehub.databinding.ListGroupBinding

class AccountingStudentAdapter : ListAdapter<Group, AccountingStudentAdapter.Holder>(AccountingStudentAdapter.Comparator()) {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }


    class Holder(view: View, listener: onItemClickListener) : RecyclerView.ViewHolder(view) {
        private val binding = ListGroupBinding.bind(view)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

        fun bind(group: Group) = with(binding) {
            groupNameTextView.text = group.name
        }
    }

    class Comparator : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_group, parent, false)
        return Holder(view, mListener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}