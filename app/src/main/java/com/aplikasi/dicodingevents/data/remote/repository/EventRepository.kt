package com.aplikasi.dicodingevents.data.remote.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.aplikasi.dicodingevents.data.local.EventDao
import com.aplikasi.dicodingevents.data.local.EventEntity
import com.aplikasi.dicodingevents.data.remote.retrofit.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao,
) {
    private val resultUpcoming = MediatorLiveData<Result<List<EventEntity>>>()
    private val resultFinished = MediatorLiveData<Result<List<EventEntity>>>()

    fun getUpcomingEvents(): LiveData<Result<List<EventEntity>>> {
        resultUpcoming.value = Result.Loading

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getUpcomingEvents(1, 40)
                val listEvents = response.listEvents

                Log.d("CheckRepository", "Fetched Events from API: $listEvents")

                val eventList = listEvents.mapNotNull { listEvent ->
                    listEvent.id?.let { id ->
                        val isFavorited = eventDao.isEventsFavorited(id)
                        val defaultStatus = "upcoming"
                        EventEntity.fromListEventsItem(listEvent).apply {
                            this.isFavorited = isFavorited
                            this.status = defaultStatus
                        }
                    }
                }
                eventDao.deleteUpcomingAll()
                eventDao.insertEvents(eventList)

                Log.d("CheckRepository", "Inserted Events: ${eventList.size}")

                withContext(Dispatchers.Main) {
                    resultUpcoming.addSource(eventDao.getUpcomingEvents()) { newData ->
                        Log.d("CheckRepository", "Upcoming Events: $newData")
                        resultUpcoming.value = Result.Success(newData)
                    }
                }
            } catch (e: Exception) {
                Log.e("EventRepository", "getUpcomingEvents: ${e.message}")
                withContext(Dispatchers.Main) {
                    resultUpcoming.value =
                        Result.Error("Error fetching upcoming events: ${e.message}")
                }
            }
        }
        return resultUpcoming
    }

    fun getFinishedEvents(): LiveData<Result<List<EventEntity>>> {
        resultFinished.value = Result.Loading

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getFinishedEvents(0, 40)
                val listEvents = response.listEvents
                val eventList = listEvents.mapNotNull { listEvent ->
                    listEvent.id?.let { id ->
                        val isFavorited = eventDao.isEventsFavorited(id)
                        val defaultStatus = "finished"
                        EventEntity.fromListEventsItem(listEvent).apply {
                            this.isFavorited = isFavorited
                            this.status = defaultStatus
                        }
                    }
                }

                eventDao.deleteFinishedAll()
                eventDao.insertEvents(eventList)

                withContext(Dispatchers.Main) {
                    resultFinished.addSource(eventDao.getFinishedEvents()) { newData ->
                        Log.d("CheckRepository", "Finished Events: $newData")
                        resultFinished.value = Result.Success(newData)
                    }
                }
            } catch (e: Exception) {
                Log.e("EventRepository", "getFinishedEvents: ${e.message}")
                withContext(Dispatchers.Main) {
                    resultFinished.value =
                        Result.Error("Error fetching finished events: ${e.message}")
                }
            }
        }
        return resultFinished
    }

    fun getFavoriteEvents(): LiveData<List<EventEntity>> {
        return eventDao.getFavoritedEvents()
    }

    fun searchEvents(query: String): LiveData<Result<List<EventEntity>>> {
        val resultSearch = MediatorLiveData<Result<List<EventEntity>>>()
        resultSearch.value = Result.Loading
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val searchResults = eventDao.searchAllEvents(query)
                Log.d("EventRepository", "Search Results Found: $searchResults")
                withContext(Dispatchers.Main) {
                    resultSearch.value = Result.Success(searchResults)
                }
            } catch (e: Exception) {
                Log.e("EventRepository", "searchEvents: ${e.message}")
                withContext(Dispatchers.Main) {
                    resultSearch.value = Result.Error("Error searching events: ${e.message}")
                }
            }
        }


        return resultSearch
    }

    fun getDetailEvent(id: Int): LiveData<Result<EventEntity>> {
        val resultDetail = MediatorLiveData<Result<EventEntity>>()
        resultDetail.value = Result.Loading

        val localData = eventDao.getDetailEvent(id)
        resultDetail.addSource(localData) { detail ->
            detail?.let {
                resultDetail.value = Result.Success(it)
            } ?: run {
                resultDetail.value = Result.Error("Event detail not found")
            }
        }

        return resultDetail
    }

    suspend fun setEventFavorite(events: EventEntity, favoriteState: Boolean) {
        events.isFavorited = favoriteState
        eventDao.updateEvents(events)
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao)
            }.also { instance = it }
    }
}
