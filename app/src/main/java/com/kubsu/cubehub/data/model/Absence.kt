package com.kubsu.cubehub.data.model

data class Absence(
    val student: StudentAccounting,
    val absenceDate: String,
    val absenceType: AbsenceType
)
