package com.aplikasi.dicodingevents.ui.detailEvent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.aplikasi.dicodingevents.R
import com.aplikasi.dicodingevents.data.response.Event
import com.aplikasi.dicodingevents.databinding.ActivityDetailEventBinding
import com.bumptech.glide.Glide

class DetailEvent : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventBinding

    private val viewModel: DetailEventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showLoading(true)
        hideEventInfo()

        val eventId = intent.getIntExtra("EXTRA_EVENT_ID", -1)
        if (savedInstanceState == null) {
            if (eventId != -1) {
                viewModel.fetchDetailEvent(eventId)

                viewModel.detailEvent.observe(this) { event ->
                    if (event != null) {
                        bindEventData(event)
                    }
                    showLoading(false)
                }
            } else {
                showLoading(false)
            }
        }
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

    private fun hideEventInfo() {
        with(binding) {
            nameEvent.visibility = View.GONE
            ownerInfo.visibility = View.GONE
            dateText.visibility = View.GONE
            quotaText.visibility = View.GONE
            detailDeskripsi.visibility = View.GONE
            detailSummary.visibility = View.GONE
        }
    }

    private fun bindEventData(event: Event) {
        with(binding) {
            nameEvent.text = event.name
            ownerInfo.text = event.ownerName
            dateText.text = event.beginTime
            val remainingQuota = event.quota?.minus(event.registrants ?: 0) ?: 0
            quotaText.text = "$remainingQuota"
            detailDeskripsi.text = event.description?.let {
                HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
            } ?: ""

            detailSummary.text = event.summary

            nameEvent.visibility = View.VISIBLE
            ownerInfo.visibility = View.VISIBLE
            dateText.visibility = View.VISIBLE
            quotaText.visibility = View.VISIBLE
            detailDeskripsi.visibility = View.VISIBLE
            detailSummary.visibility = View.VISIBLE
            tvImageEvent.visibility = View.VISIBLE
        }

        val tvImageEvent: ImageView = binding.tvImageEvent
        Glide.with(this)
            .load(event.imageLogo ?: event.mediaCover)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(tvImageEvent)

        binding.buttonRegister.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(event.link)
            }
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
