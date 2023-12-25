package com.kubsu.cubehub.data.network

import com.kubsu.cubehub.data.model.Course
import com.kubsu.cubehub.data.model.Group
import com.kubsu.cubehub.data.model.Timetable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query
import java.time.OffsetDateTime

interface MainService {

    @Headers("Content-Type: application/json")
    @GET("timetables")
    suspend fun getTimetables(@Header("Authorization") token: String,
                              @Query("start_date") startDate: OffsetDateTime,
                              @Query("end_date") endDate: OffsetDateTime
    ): List<Timetable>

    @Headers("Content-Type: application/json")
    @GET("users/groups")
    suspend fun getGroups(@Header("Authorization") token: String): List<Group>

    @Headers("Content-Type: application/json")
    @GET("accounting/lecturers/courses")
    suspend fun getLecturerCourses(@Header("Authorization") token: String): List<Course>
}