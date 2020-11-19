package com.example.dropspot.fragments.spotDetail

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
import com.example.dropspot.R
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
    private var currentSpotDetail: SpotDetail? = null
    private lateinit var criterionScoreAdapter: CriterionScoreAdapter
    private val args: SpotDetailFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpotDetailBinding.inflate(inflater)
        binding.vm = spotDetailViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        criterionScoreAdapter = CriterionScoreAdapter(spotDetailViewModel)
        binding.ratingList.adapter = criterionScoreAdapter


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
                    Log.i(TAG, "favorite")

                    currentSpotDetail!!.liked = true
                    binding.spotDetail = currentSpotDetail
                    spotDetailViewModel.favoriteOrUnFavorite(currentSpotDetail!!.spotId, true)

                } else {
                    Log.i(TAG, "unfavorite")

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
            v.text = resources.getString(
                R.string.spot_detail_latitude_longitude_representation,
                detail.latitude,
                detail.longitude
            )
        }
    }





}