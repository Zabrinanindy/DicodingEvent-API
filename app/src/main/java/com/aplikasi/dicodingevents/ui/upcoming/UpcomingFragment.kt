package com.aplikasi.dicodingevents.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aplikasi.dicodingevents.ui.detailEvent.DetailEvent
import com.aplikasi.dicodingevents.data.adapter.EventAdapter
import com.aplikasi.dicodingevents.data.response.ListEventsItem
import com.aplikasi.dicodingevents.databinding.FragmentUpcomingBinding

class UpcomingFragment : Fragment() {
    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventAdapter: EventAdapter
    private lateinit var viewModel: UpcomingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(UpcomingViewModel::class.java)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpcomingEvent.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvUpcomingEvent.addItemDecoration(itemDecoration)

        eventAdapter = EventAdapter(mutableListOf()) { event -> onEventClicked(event) }
        binding.rvUpcomingEvent.adapter = eventAdapter

        observeViewModel()

        viewModel.fetchEvent()
    }

    private fun observeViewModel() {
        viewModel.upcomingEvent.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Log.e("UpcomingFragment", it)
            }
        }
    }

    private fun onEventClicked(event: ListEventsItem) {
        val intent = Intent(requireActivity(), DetailEvent::class.java)
        intent.putExtra("EXTRA_EVENT_ID", event.id)
        startActivity(intent)
        Log.d("UpcomingFragment", "Event clicked: ${event.name}")
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
