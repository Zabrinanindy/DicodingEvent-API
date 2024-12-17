package com.aplikasi.dicodingevents.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    fun getEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM events where status = 'upcoming'")
    fun getUpcomingEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM events where status = 'finished' ORDER BY beginTime DESC")
    fun getFinishedEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id ")
    fun getDetailEvent(id: Int): LiveData<EventEntity>

    @Query("SELECT * FROM events where favorited = 1")
    fun getFavoritedEvents(): LiveData<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvents(news: List<EventEntity>)

    @Update
    suspend fun updateEvents(news: EventEntity)

    @Query("DELETE FROM events WHERE status = 'upcoming'")
    suspend fun deleteUpcomingAll()

    @Query("DELETE FROM events WHERE status = 'finished'")
    suspend fun deleteFinishedAll()

    @Query("DELETE FROM events WHERE favorited = 0")
    suspend fun deleteAll()

    @Query("SELECT EXISTS(SELECT * FROM events WHERE id = :id AND favorited = 1)")
    suspend fun isEventsFavorited(id: Int): Boolean

    @Query("SELECT * FROM events WHERE name OR ownerName LIKE '%' || :query || '%'")
    suspend fun searchAllEvents(query: String): List<EventEntity>

}