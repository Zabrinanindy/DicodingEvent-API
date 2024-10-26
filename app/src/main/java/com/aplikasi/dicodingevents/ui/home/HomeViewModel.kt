package com.aplikasi.dicodingevents.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aplikasi.dicodingevents.data.response.EventResponse
import com.aplikasi.dicodingevents.data.response.ListEventsItem
import com.aplikasi.dicodingevents.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class HomeViewModel : ViewModel() {

    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _searchResults = MutableLiveData<List<ListEventsItem>>()
    val searchResults: LiveData<List<ListEventsItem>> = _searchResults

    private val _isSearchLoading = MutableLiveData<Boolean>()
    val isSearchLoading: LiveData<Boolean> get() = _isSearchLoading

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    init {
        fetchEvents() }

    fun fetchEvents() {
        _isLoading.value = true
        _errorMessage.value = null

        val clientUpcoming = ApiConfig.getApiService().getEvents(active = 1)
        clientUpcoming.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    _upcomingEvents.value = response.body()?.listEvents ?: emptyList()
                } else {
                    _errorMessage.value = "Terjadi kesalahan: ${response.message()}"
                }
                checkLoadingComplete()
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                handleError(t)
                checkLoadingComplete()
            }
        })

        val clientFinished = ApiConfig.getApiService().getEvents(active = 0)
        clientFinished.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    _finishedEvents.value = response.body()?.listEvents ?: emptyList()
                } else {
                    _errorMessage.value = "Terjadi kesalahan: ${response.message()}"
                }
                checkLoadingComplete()
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                handleError(t)
                checkLoadingComplete()
            }
        })
    }

    private fun searchEvents(query: String) {
        _isSearchLoading.value = true
        _errorMessage.value = null

        val clientSearch = ApiConfig.getApiService().searchEvents(active = -1, keyword = query)
        clientSearch.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    _searchResults.value = response.body()?.listEvents ?: emptyList()
                } else {
                    _errorMessage.value = "Terjadi kesalahan: ${response.message()}"
                }
                _isSearchLoading.value = false
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                handleError(t)
                _isSearchLoading.value = false
            }
        })
    }
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            searchEvents(query)
        } else {
            resetSearch()
        }
    }

    fun resetSearch() {
        _searchResults.value = emptyList()
    }

    private fun checkLoadingComplete() {
        if (_upcomingEvents.value != null && _finishedEvents.value != null) {
            _isLoading.value = false
        }
    }
    private fun handleError(t: Throwable) {
        val message = when (t) {
            is UnknownHostException -> "Maaf, tidak ada koneksi internet"
            is SocketTimeoutException -> "Koneksi internet Anda terlalu lama"
            else -> "Terjadi kesalahan: ${t.localizedMessage}"
        }
        _errorMessage.value = message
        Log.e("HomeViewModel", "onFailure: ${t.message}")
    }
}
