package com.aplikasi.dicodingevents.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aplikasi.dicodingevents.data.remote.repository.Result
import com.aplikasi.dicodingevents.ui.detailEvent.DetailEvent
import com.aplikasi.dicodingevents.data.adapter.EventAdapter
import com.aplikasi.dicodingevents.data.local.EventEntity
import com.aplikasi.dicodingevents.databinding.FragmentUpcomingBinding
import com.aplikasi.dicodingevents.ui.detailEvent.DetailEvent.Companion.EXTRA_EVENT_ID


class UpcomingFragment : Fragment() {
    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventAdapter: EventAdapter
    private val viewModel: UpcomingViewModel by viewModels {
        UpcomingViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeData()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpcomingEvent.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvUpcomingEvent.addItemDecoration(itemDecoration)

        eventAdapter = EventAdapter(
            { event -> onFavoriteClick(event) },
            { event -> onEventClicked(event) }
        )
        binding.rvUpcomingEvent.adapter = eventAdapter
    }

    private fun observeData() {
        viewModel.upcomingEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val eventData = result.data
                    eventAdapter.submitList(eventData)
                    if (eventData.isEmpty()) {
                        Toast.makeText(context, "Event tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        context,
                        "Terjadi kesalahan: ${result.errorMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Status tidak dikenali", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onEventClicked(event: EventEntity) {
        val intent = Intent(requireActivity(), DetailEvent::class.java)
        intent.putExtra(EXTRA_EVENT_ID, event.id)
        startActivity(intent)
        Log.d("UpcomingFragment", "Event clicked: ${event.name}")
    }

    private fun onFavoriteClick(event: EventEntity) {
        if (event.isFavorited) {
            viewModel.deleteFavoritedEvent(event)
            Toast.makeText(context, "Event removed from favorites", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.toggleFavorite(event)
            Toast.makeText(context, "Event added to favorites", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
