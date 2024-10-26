package com.aplikasi.dicodingevents.retrofit
import com.aplikasi.dicodingevents.data.response.DetailEventResponse
import com.aplikasi.dicodingevents.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("events")
    fun getEvents(
        @Query("active") active: Int
    ): Call<EventResponse>

    @GET("events")
    fun searchEvents(
        @Query("q") keyword: String,
        @Query("active") active: Int = -1
    ): Call<EventResponse>

    @GET("events/{id}")
    fun getDetailEvent(@Path("id") eventId: Int
    ): Call<DetailEventResponse>
}