package com.aplikasi.dicodingevents.ui.favorite

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
import com.aplikasi.dicodingevents.ui.detailEvent.DetailEvent
import com.aplikasi.dicodingevents.data.adapter.EventAdapter
import com.aplikasi.dicodingevents.data.local.EventEntity
import com.aplikasi.dicodingevents.databinding.FragmentFavoriteBinding
import com.aplikasi.dicodingevents.ui.detailEvent.DetailEvent.Companion.EXTRA_EVENT_ID

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventAdapter: EventAdapter
    private val viewModel: FavoriteViewModel by viewModels {
        FavoriteViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeData()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavoriteEvent.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvFavoriteEvent.addItemDecoration(itemDecoration)

        eventAdapter = EventAdapter(
            { event -> onFavoriteClick(event) },
            { event -> onEventClicked(event) }
        )
        binding.rvFavoriteEvent.adapter = eventAdapter
    }

    private fun observeData() {
        viewModel.favoriteEvents.observe(viewLifecycleOwner) { result ->
            eventAdapter.submitList(result?.toList())
            binding.progressBar.visibility = View.GONE
            binding.tvEmptyData.visibility = if (result.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun onEventClicked(event: EventEntity) {
        val intent = Intent(requireActivity(), DetailEvent::class.java)
        intent.putExtra(EXTRA_EVENT_ID, event.id)
        startActivity(intent)
        Log.d("FavoriteFragment", "Event clicked: ${event.name}")
    }

    private fun onFavoriteClick(event: EventEntity) {
        viewModel.toggleFavorite(event)
        Toast.makeText(context, "Event removed from favorites", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
