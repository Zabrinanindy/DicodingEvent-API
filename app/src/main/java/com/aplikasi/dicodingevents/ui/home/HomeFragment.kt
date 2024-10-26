package com.aplikasi.dicodingevents.ui.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aplikasi.dicodingevents.ui.detailEvent.DetailEvent
import com.aplikasi.dicodingevents.data.adapter.EventAdapter
import com.aplikasi.dicodingevents.data.response.ListEventsItem
import com.aplikasi.dicodingevents.databinding.FragmentHomeBinding



class HomeFragment : Fragment() {

    private lateinit var upcomingAdapter: EventAdapter
    private lateinit var finishedAdapter: EventAdapter
    private lateinit var searchAdapter: EventAdapter

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupSearch()
        observeViewModel()
        fetchEvents()
    }

    private fun setupAdapters() {
        searchAdapter = EventAdapter(mutableListOf()) { event -> onEventClicked(event)}
        binding.rvSearchEvent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            val itemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
            addItemDecoration(itemDecoration)
            adapter = searchAdapter
        }

        upcomingAdapter = EventAdapter (mutableListOf()) { event -> onEventClicked(event) }
        binding.rvUpcomingEvent.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingAdapter
        }

        finishedAdapter = EventAdapter (mutableListOf()) { event -> onEventClicked(event) }
        binding.rvFinishedEvent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            val itemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
            addItemDecoration(itemDecoration)
            adapter = finishedAdapter
        }
    }

    private fun setupSearch() {
        val searchBar = binding.searchBar
        val searchView = binding.searchView
        val rvSearchEvent = binding.rvSearchEvent
        val searchProgressBar = binding.searchProgressBar

        searchView.setupWithSearchBar(searchBar)
        searchView.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    searchProgressBar.visibility = View.VISIBLE
                    rvSearchEvent.visibility = View.VISIBLE
                    homeViewModel.setSearchQuery(query)
                } else {
                    searchProgressBar.visibility = View.GONE
                    rvSearchEvent.visibility = View.GONE
                    homeViewModel.resetSearch()
                }
            }
        })

        searchBar.setOnClickListener {
            Log.d(TAG, "SearchBar clicked")
            binding.searchView.visibility = View.VISIBLE
            binding.searchView.show()
        }
    }

    private fun observeViewModel() {
        homeViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            upcomingAdapter.submitList(events)
        }

        homeViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            finishedAdapter.submitList(events)
        }

        homeViewModel.isSearchLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d(TAG, "isSearchLoading: $isLoading")
            if (isLoading) {
                binding.searchProgressBar.visibility = View.VISIBLE
            } else {
                binding.searchProgressBar.visibility = View.GONE
            }
        }

        homeViewModel.searchResults.observe(viewLifecycleOwner) { events ->
            searchAdapter.submitList(events)
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Log.e(TAG, "Error: $it")
            }
        }
    }

    private fun onEventClicked(event: ListEventsItem) {
        val intent = Intent(requireActivity(), DetailEvent::class.java)
        intent.putExtra("EXTRA_EVENT_ID", event.id)
        startActivity(intent)
        Log.d("HomeFragment", "Event clicked: ${event.name}")

    }

    private fun fetchEvents() {
        homeViewModel.fetchEvents()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
