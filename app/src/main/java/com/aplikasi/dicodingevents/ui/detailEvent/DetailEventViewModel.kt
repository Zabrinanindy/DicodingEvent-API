package com.aplikasi.dicodingevents.ui.detailEvent

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aplikasi.dicodingevents.data.response.DetailEventResponse
import com.aplikasi.dicodingevents.data.response.Event
import com.aplikasi.dicodingevents.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class DetailEventViewModel : ViewModel() {

    private val _detailEvent = MutableLiveData<Event>()
    val detailEvent: LiveData<Event> get() = _detailEvent

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> get() = _error

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    fun fetchDetailEvent(id: Int) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailEvent(id)
        client.enqueue(object : Callback<DetailEventResponse> {
            override fun onResponse(call: Call<DetailEventResponse>, response: Response<DetailEventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _detailEvent.value = response.body()?.event
                } else {
                    _error.value = true
                    _message.value = "Error: ${response.message()}"
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = true
                handleError(t)
            }

            private fun handleError(t: Throwable) {
                val message = when (t) {
                    is UnknownHostException -> "Maaf, koneksi internet Anda lambat atau tidak ada"
                    is SocketTimeoutException -> "Koneksi internet Anda terlalu lambat"
                    else -> "Terjadi kesalahan: ${t.localizedMessage}"
                }
                _message.value = message
                Log.e("DetailEventViewModel", "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        private const val TAG = "DetailEventViewModel"
    }
}
