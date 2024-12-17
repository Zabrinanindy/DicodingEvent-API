package com.aplikasi.dicodingevents.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aplikasi.dicodingevents.ui.detailEvent.DetailEvent
import com.aplikasi.dicodingevents.data.adapter.EventAdapter
import com.aplikasi.dicodingevents.data.local.EventEntity
import com.aplikasi.dicodingevents.databinding.FragmentHomeBinding
import com.aplikasi.dicodingevents.data.remote.repository.Result

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory.getInstance(requireContext())
    }

    private val upcomingAdapter by lazy {
        EventAdapter(
            onItemClick = { event -> navigateToDetail(event.id) },
            onFavoriteClick = { event -> toggleFavorite(event) }
        )
    }

    private val finishedAdapter by lazy {
        EventAdapter(
            onItemClick = { event -> navigateToDetail(event.id) },
            onFavoriteClick = { event -> toggleFavorite(event) }
        )
    }

    private val searchAdapter by lazy {
        EventAdapter(
            onItemClick = { event -> navigateToDetail(event.id) },
            onFavoriteClick = { event -> toggleFavorite(event) }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchEvents()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel.isUpcomingLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.upcomingLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isFinishedLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.finishedLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupSearch()
        observeData()

    }

    private fun setupRecyclerViews() {
        binding.rvUpcomingEvent.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingAdapter
        }

        binding.rvFinishedEvent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = finishedAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        binding.rvSearchEvent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
    }

    private fun setupSearch() {
        binding.searchView.setupWithSearchBar(binding.searchBar)

        binding.searchView.editText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = textView.text.toString()
                performSearch(query)
                closeSearchBar()
                true
            } else {
                false
            }
        }

        binding.searchBar.setOnClickListener {
            binding.searchView.visibility = View.VISIBLE
            binding.searchView.show()
        }
    }

    private fun performSearch(query: String) {
        if (query.isNotBlank()) {
            viewModel.setSearchQuery(query)
            binding.rvSearchEvent.visibility = View.VISIBLE
        } else {
            viewModel.resetSearch()
            binding.rvSearchEvent.visibility = View.GONE
        }
        closeSearchBar()
    }

    private fun closeSearchBar() {
        binding.searchView.clearFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
    }

    private fun observeData() {
        viewModel.upcomingEvents.observe(viewLifecycleOwner) { result ->
            Log.d("HomeFragment", "Upcoming Events Result: $result")
            handleResult(result, upcomingAdapter, binding.upcomingLoadingIndicator)
        }

        viewModel.finishedEvents.observe(viewLifecycleOwner) { result ->
            Log.d("HomeFragment", "Finished Events Result: $result")
            handleResult(result, finishedAdapter, binding.finishedLoadingIndicator)
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { result ->
            Log.d("HomeFragment", "Search Results: $result")
            handleResult(result, searchAdapter, binding.searchProgressBar)
            binding.rvSearchEvent.visibility = if (result is Result.Success && result.data.isNotEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let { Log.e("HomeFragment", "Error: $it") }
        }
    }

    private fun handleResult(result: Result<List<EventEntity>>, adapter: EventAdapter, progressBar: View) {
        when (result) {
            is Result.Loading -> progressBar.visibility = View.VISIBLE
            is Result.Success -> {
                progressBar.visibility = View.GONE
                adapter.submitList(result.data)
                if (result.data.isEmpty()) {
                    Toast.makeText(context, "Event tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            is Result.Error -> {
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Terjadi kesalahan: ${result.errorMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.upcomingLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.finishedLoadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.searchProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun navigateToDetail(eventId: Int) {
        val intent = Intent(requireActivity(), DetailEvent::class.java)
        intent.putExtra(DetailEvent.EXTRA_EVENT_ID, eventId)
        startActivity(intent)
    }

    private fun toggleFavorite(event: EventEntity) {
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
