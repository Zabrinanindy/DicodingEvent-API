package com.aplikasi.dicodingevents.ui.upcoming

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

class UpcomingViewModel : ViewModel() {

    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvent: LiveData<List<ListEventsItem>> = _upcomingEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchEvent() {
        _isLoading.value = true
        _errorMessage.value = null

        val client =  ApiConfig.getApiService().getEvents(active = 1)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _upcomingEvents.value = response.body()?.listEvents ?: emptyList()
                } else {
                    _errorMessage.value = "Terjadi kesalahan: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                handleError(t)
            }
        })
    }

    private fun handleError(t: Throwable) {
        val message = when (t) {
            is UnknownHostException -> "Maaf, koneksi internet tidak ada"
            is SocketTimeoutException -> "Koneksi internet lemot"
            else -> "Terjadi kesalahan: ${t.localizedMessage}"
        }
        _errorMessage.value = message
        Log.e("UpcomingViewModel", "onFailure: ${t.message}")
    }
}
