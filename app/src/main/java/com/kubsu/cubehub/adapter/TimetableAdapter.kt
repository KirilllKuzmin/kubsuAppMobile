package com.kubsu.cubehub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kubsu.cubehub.databinding.ListTimetableBinding
import com.kubsu.cubehub.R
import com.kubsu.cubehub.data.model.Timetable
import java.time.format.DateTimeFormatter

class TimetableAdapter : ListAdapter<Timetable, TimetableAdapter.Holder>(Comparator()) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ListTimetableBinding.bind(view)

        fun bind(timetable: Timetable) = with(binding) {
            courseNumberTextView.text = timetable.numberTimeClassHeld.id.toString()
            courseTypeTextView.text = timetable.course.courseType.name
            courseTimeTextView.text = timetable.numberTimeClassHeld.startTime.format(
                DateTimeFormatter.ofPattern("HH:mm")).toString() +
                    " - " +
                    timetable.numberTimeClassHeld.endTime.format(
                        DateTimeFormatter.ofPattern("HH:mm")).toString()
            courseNameTextView.text = timetable.course.name
            courseGroupTextView.text = timetable.timetableGroup.joinToString(", ") { it.name }
            classroomTextView.text = timetable.classroom.classroomNumber
        }
    }

    class Comparator : DiffUtil.ItemCallback<Timetable>() {
        override fun areItemsTheSame(oldItem: Timetable, newItem: Timetable): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Timetable, newItem: Timetable): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_timetable, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}