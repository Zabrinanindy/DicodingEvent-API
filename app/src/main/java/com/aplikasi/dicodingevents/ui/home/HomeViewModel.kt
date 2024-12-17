package com.aplikasi.dicodingevents.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aplikasi.dicodingevents.data.remote.repository.EventRepository
import com.aplikasi.dicodingevents.data.remote.repository.Result
import com.aplikasi.dicodingevents.data.local.EventEntity
import kotlinx.coroutines.launch

class HomeViewModel(private val eventsRepository: EventRepository) : ViewModel() {
    var upcomingEvents: LiveData<Result<List<EventEntity>>> = eventsRepository.getUpcomingEvents()
    var finishedEvents: LiveData<Result<List<EventEntity>>> = eventsRepository.getFinishedEvents()

    private val _searchResults = MutableLiveData<Result<List<EventEntity>>>()
    val searchResults: LiveData<Result<List<EventEntity>>> get() = _searchResults

    private val _isUpcomingLoading = MutableLiveData<Boolean>()
    val isUpcomingLoading: LiveData<Boolean> get() = _isUpcomingLoading

    private val _isFinishedLoading = MutableLiveData<Boolean>()
    val isFinishedLoading: LiveData<Boolean> get() = _isFinishedLoading

    private val _isSearchLoading = MutableLiveData<Boolean>()
    val isSearchLoading: LiveData<Boolean> get() = _isSearchLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        fetchEvents()
    }

    fun fetchEvents() {
        viewModelScope.launch {
            _isUpcomingLoading.value = true
            upcomingEvents = eventsRepository.getUpcomingEvents().also {
                _isUpcomingLoading.value = false
            }

            _isFinishedLoading.value = true
            finishedEvents = eventsRepository.getFinishedEvents().also {
                _isFinishedLoading.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _isSearchLoading.value = true
        viewModelScope.launch {
            eventsRepository.searchEvents(query).observeForever { result ->
                _searchResults.value = result
                _isSearchLoading.value = false
            }
        }
    }

    fun toggleFavorite(event: EventEntity) {
        viewModelScope.launch {
            eventsRepository.setEventFavorite(event, !event.isFavorited)
        }
    }

    fun deleteFavoritedEvent(event: EventEntity) {
        viewModelScope.launch {
            eventsRepository.setEventFavorite(event, false)
        }
    }

    fun resetSearch() {
        _searchResults.value = Result.Success(emptyList())
    }
}
