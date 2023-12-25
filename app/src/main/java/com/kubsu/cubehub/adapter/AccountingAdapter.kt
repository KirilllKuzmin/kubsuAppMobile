package com.kubsu.cubehub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kubsu.cubehub.R
import com.kubsu.cubehub.data.model.Course
import com.kubsu.cubehub.databinding.ListCourseBinding

class AccountingAdapter : ListAdapter<Course, AccountingAdapter.Holder>(AccountingAdapter.Comparator()) {
    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ListCourseBinding.bind(view)

        fun bind(course: Course) = with(binding) {
            courseTypeTextView.text = course.courseType.name
            courseNameTextView.text = course.name
        }
    }

    class Comparator : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_course, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}