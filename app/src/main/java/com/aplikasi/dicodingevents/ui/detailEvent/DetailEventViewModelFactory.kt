package com.aplikasi.dicodingevents.ui.detailEvent

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aplikasi.dicodingevents.data.remote.repository.EventRepository
import com.aplikasi.dicodingevents.data.di.Injection

class DetailEventViewModelFactory private constructor(
    private val eventRepository: EventRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DetailEventViewModel::class.java) -> {
                DetailEventViewModel(eventRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: DetailEventViewModelFactory? = null

        fun getInstance(context: Context): DetailEventViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: DetailEventViewModelFactory(
                    Injection.provideRepository(context)
                ).also { instance = it }
            }
    }
}
