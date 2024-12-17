package com.aplikasi.dicodingevents.ui.detailEvent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aplikasi.dicodingevents.data.remote.repository.EventRepository
import com.aplikasi.dicodingevents.data.remote.repository.Result
import com.aplikasi.dicodingevents.data.local.EventEntity
import kotlinx.coroutines.launch

class DetailEventViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _detailEvent = MutableLiveData<Result<EventEntity>>()
    val detailEvent: LiveData<Result<EventEntity>> get() = _detailEvent

    fun getEventDetail(eventId: Int) {
        viewModelScope.launch {
            eventRepository.getDetailEvent(eventId).observeForever { result ->
                _detailEvent.value = result
            }
        }
    }

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
