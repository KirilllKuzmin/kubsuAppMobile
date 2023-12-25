package com.kubsu.cubehub.data.model

import com.google.gson.annotations.JsonAdapter
import com.kubsu.cubehub.serialization.OffsetDateTimeDeserializer
import java.time.OffsetDateTime

data class NumberTimeClassHeld(
    val id: Long,

    @JsonAdapter(OffsetDateTimeDeserializer::class)
    val startTime: OffsetDateTime,

    @JsonAdapter(OffsetDateTimeDeserializer::class)
    val endTime: OffsetDateTime
)
