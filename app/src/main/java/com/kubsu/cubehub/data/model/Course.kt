package com.kubsu.cubehub.data.model

import java.io.Serializable

data class Course(

    val id: Long,

    val name: String,

    val courseType: CourseType
) : Serializable
