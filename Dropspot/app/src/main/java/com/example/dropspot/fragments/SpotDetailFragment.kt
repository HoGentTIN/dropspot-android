package com.example.dropspot.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.dropspot.adapters.CriterionScoresAdapter
import com.example.dropspot.data.model.SpotDetail
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
    private val args: SpotDetailFragmentArgs by navArgs()
    private lateinit var criterionScoresAdapter: CriterionScoresAdapter
    private var currentSpotDetail: SpotDetail? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpotDetailBinding.inflate(inflater)
        binding.vm = spotDetailViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        criterionScoresAdapter =
            CriterionScoresAdapter(
                spotDetailViewModel
            )
        binding.ratingList.adapter = criterionScoresAdapter


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadSpotDetail()

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

            if (currentSpotDetail != null) {

                if (!currentSpotDetail!!.liked) {

                    currentSpotDetail!!.liked = true
                    binding.spotDetail = currentSpotDetail
                    spotDetailViewModel.favoriteOrUnFavorite(currentSpotDetail!!.spotId, true)

                } else {

                    currentSpotDetail!!.liked = false
                    binding.spotDetail = currentSpotDetail
                    spotDetailViewModel.favoriteOrUnFavorite(currentSpotDetail!!.spotId, false)
                }
            }
        }

        //vote
        spotDetailViewModel.voteSuccess.observe(viewLifecycleOwner,
            Observer {
                if (it != null) {
                    Snackbar.make(requireView(), it.message, Snackbar.LENGTH_SHORT).show()
                }
            }
        )

        //favor
        spotDetailViewModel.favoriteSuccess.observe(viewLifecycleOwner,
            Observer {
                if (it != null) {
                    Snackbar.make(requireView(), it.message, Snackbar.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadSpotDetail() {
        val spotId = args.spotId
        spotDetailViewModel.setSpotId(spotId)
        val liveData = spotDetailViewModel.getSpotDetail()
        liveData.observe(viewLifecycleOwner, Observer {
            Log.i(TAG, "spot_detail: $it")
            if (it != null) {
                binding.spotDetail = it
                currentSpotDetail = it
                binding.navigateIcon.alpha = 1F
                criterionScoresAdapter.submitList(it.criteriaScore)
            }
        })
    }






}