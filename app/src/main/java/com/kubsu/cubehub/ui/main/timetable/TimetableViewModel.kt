package com.kubsu.cubehub.ui.main.timetable

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimetableViewModel : ViewModel() {

    val token = MutableLiveData<String>()
}