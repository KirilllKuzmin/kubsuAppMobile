package com.kubsu.cubehub.data.network

import com.kubsu.cubehub.data.model.Absence
import com.kubsu.cubehub.data.model.AbsenceRequestBody
import com.kubsu.cubehub.data.model.Course
import com.kubsu.cubehub.data.model.Group
import com.kubsu.cubehub.data.model.Student
import com.kubsu.cubehub.data.model.Timetable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.OffsetDateTime
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

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

    @Headers("Content-Type: application/json")
    @GET("accounting/lecturers/courses/{courseId}/groups")
    suspend fun getCourseGroups(@Header("Authorization") token: String,
                                @Path("courseId") courseId: Long): List<Group>

    @Headers("Content-Type: application/json")
    @GET("accounting/groups/{groupId}/students")
    suspend fun getStudents(@Header("Authorization") token: String,
                            @Path("groupId") groupId: Long): List<Student>

    @Headers("Content-Type: application/json")
    @POST("accounting/lecturers/absences")
    suspend fun setAbsences(@Header("Authorization") token: String,
                            @Body requestBody: AbsenceRequestBody): JvmType.Object //TODO Добавить API по отправке списка, а не элемента!!!

    @Headers("Content-Type: application/json")
    @GET("accounting/lecturers/absences/courses/{courseId}/groups/{groupId}")
    suspend fun getAbsences(@Header("Authorization") token: String,
                            @Path("courseId") courseId: Long,
                            @Path("groupId") groupId: Long): List<Absence>
}