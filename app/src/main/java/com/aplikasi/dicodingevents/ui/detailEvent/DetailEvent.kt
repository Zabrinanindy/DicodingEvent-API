package com.aplikasi.dicodingevents.ui.detailEvent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.aplikasi.dicodingevents.R
import com.aplikasi.dicodingevents.data.remote.repository.Result
import com.aplikasi.dicodingevents.data.local.EventEntity
import com.aplikasi.dicodingevents.databinding.ActivityDetailEventBinding
import com.bumptech.glide.Glide

class DetailEvent : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventBinding
    private val viewModel: DetailEventViewModel by viewModels {
        DetailEventViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1)
        if (eventId != -1) {
            viewModel.getEventDetail(eventId)
        } else {
            Toast.makeText(this, "Invalid Event ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.detailEvent.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    showEventDetail(result.data)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showError(result.errorMessage)
                }
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showEventDetail(event: EventEntity) {
        binding.apply {
            nameEvent.text = event.name
            ownerInfo.text = event.ownerName
            dateText.text = event.beginTime
            val remainingQuota = event.quota - (event.registrants)
            quotaText.text = "$remainingQuota"
            detailDeskripsi.text = HtmlCompat.fromHtml(
                event.description, HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            detailSummary.text = event.summary

            val tvImageEvent: ImageView = binding.tvImageEvent
            Glide.with(this@DetailEvent)
                .load(event.imageLogo)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(tvImageEvent)

            buttonRegister.setOnClickListener {
                openEventLink(event.link)
            }

            updateFavoriteIcon(event.isFavorited)
            tvFavorite.setOnClickListener {
                toggleFavoriteStatus(event)
            }
        }
    }

    private fun openEventLink(link: String?) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(intent)
        } catch (e: Exception) {
            showError("Couldn't open the event link")
        }
    }

    private fun toggleFavoriteStatus(event: EventEntity) {
        if (event.isFavorited) {
            viewModel.deleteFavoritedEvent(event)
        } else {
            viewModel.toggleFavorite(event)
        }
        updateFavoriteIcon(!event.isFavorited)
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        binding.tvFavorite.setImageResource(
            if (isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_EVENT_ID = "event_id"
    }
}
