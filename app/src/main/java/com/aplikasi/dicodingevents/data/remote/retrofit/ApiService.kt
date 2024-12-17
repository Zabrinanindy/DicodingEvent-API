package com.aplikasi.dicodingevents.data.remote.retrofit

import com.aplikasi.dicodingevents.data.remote.response.DetailEventResponse
import com.aplikasi.dicodingevents.data.remote.response.EventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int
    ): EventResponse

    @GET("events")
    suspend fun getUpcomingEvents(
        @Query("active") active: Int,
        @Query("limit") limit: Int
    ): EventResponse

    @GET("events")
    suspend fun getFinishedEvents(
        @Query("active") active: Int,
        @Query("limit") limit: Int
    ): EventResponse

    @GET("events")
    suspend fun searchEvents(
        @Query("q") keyword: String,
        @Query("active") active: Int = -1
    ): EventResponse

    @GET("events/{id}")
    suspend fun getDetailEvent(
        @Path("id") eventId: Int
    ): DetailEventResponse
}
