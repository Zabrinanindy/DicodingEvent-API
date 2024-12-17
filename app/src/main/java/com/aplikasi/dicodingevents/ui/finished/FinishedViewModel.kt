package com.aplikasi.dicodingevents.ui.finished

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aplikasi.dicodingevents.data.remote.repository.EventRepository
import com.aplikasi.dicodingevents.data.remote.repository.Result
import com.aplikasi.dicodingevents.data.local.EventEntity
import kotlinx.coroutines.launch

class FinishedViewModel(private val eventRepository: EventRepository) : ViewModel() {

    val finishedEvents: LiveData<Result<List<EventEntity>>> = eventRepository.getFinishedEvents()

    fun toggleFavorite(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.setEventFavorite(event, !event.isFavorited)
        }
    }

    fun deleteFavoritedEvent(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.setEventFavorite(event, false)
        }
    }
}
