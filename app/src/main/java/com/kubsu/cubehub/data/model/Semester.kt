package com.kubsu.cubehub.data.model

import com.google.gson.annotations.JsonAdapter
import com.kubsu.cubehub.serialization.OffsetDateTimeDeserializer
import java.time.OffsetDateTime

data class Semester(
    val id: Long,

    val name: String,

    @JsonAdapter(OffsetDateTimeDeserializer::class)
    val startDate: OffsetDateTime,

    @JsonAdapter(OffsetDateTimeDeserializer::class)
    val endDate: OffsetDateTime
)
