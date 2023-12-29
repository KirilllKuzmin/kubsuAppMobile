package com.kubsu.cubehub.data.model

data class AbsenceRequestBody(
    val absenceDate: String,
    val absenceTypeId: Any,
    val courseId: Long,
    val studentId: Long
)
