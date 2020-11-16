package com.example.dropspot.controllers.spotDetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.dropspot.R
import com.example.dropspot.data.model.dto.SpotDetail
import com.example.dropspot.databinding.FragmentSpotDetailBinding
import com.example.dropspot.viewmodels.SpotDetailViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel


class SpotDetailFragment : Fragment() {

    companion object {
        private val TAG = "spot_detail_fragment"
    }

    private lateinit var binding: FragmentSpotDetailBinding
    private val spotDetailViewModel: SpotDetailViewModel by viewModel()
    private var currentSpotDetail: SpotDetail? = null
    private lateinit var criterionScoreAdapter: CriterionScoreAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpotDetailBinding.inflate(inflater)
        binding.vm = spotDetailViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        criterionScoreAdapter = CriterionScoreAdapter(spotDetailViewModel)

        loadSpotDetail()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // maps navigate intent
        binding.navigateIcon.setOnClickListener {
            if (currentSpotDetail != null) {
                val gmmIntentUri = Uri.parse(
                    "google.navigation:q=${this.currentSpotDetail!!.latitude}" +
                            ",${this.currentSpotDetail!!.longitude}"
                )

                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

                mapIntent.setPackage("com.google.android.apps.maps")

                mapIntent.resolveActivity(requireActivity().packageManager)?.let {
                    startActivity(mapIntent)
                }
            }
        }

        // de/favorite spot
        binding.likeIcon.setOnClickListener {
            var icon = it as ImageView
            icon.setImageResource(R.drawable.ic_like_filled)
        }

        //vote
        spotDetailViewModel.voteSuccess.observe(viewLifecycleOwner,
            Observer {
                if (it != null) {
                    Snackbar.make(requireView(), it.message, Snackbar.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun loadSpotDetail() {
        binding.ratingList.adapter = criterionScoreAdapter
        val spotId = arguments!!.getLong("spotId")
        Log.i(TAG, "args_spot_id: $spotId")
        spotDetailViewModel.setSpotId(spotId)
        val liveData = spotDetailViewModel.getSpotDetail()
        liveData.observe(viewLifecycleOwner, Observer {
            Log.i(TAG, "spot_detail: $it")
            binding.spotDetail = it
            if (it != null) {
                updateUI(it)
                currentSpotDetail = it
                binding.navigateIcon.alpha = 1F
                criterionScoreAdapter.submitList(it.criteriaScore)
            }
        })
    }

    private fun updateUI(detail: SpotDetail) {
        val v = binding.locationAddress
        val isParkSpot = detail.address != null
        if (isParkSpot) {
            v.text = detail.address!!.getAddressString()
        } else {
            v.text = "LAT: ${detail.latitude}\nLONG: ${detail.longitude}"
        }
    }





}