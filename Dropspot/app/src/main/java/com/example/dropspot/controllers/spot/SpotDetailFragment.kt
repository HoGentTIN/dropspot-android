package com.example.dropspot.controllers.spot

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.dropspot.data.model.SpotDetail
import com.example.dropspot.databinding.FragmentSpotDetailBinding
import com.example.dropspot.viewmodels.SpotDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SpotDetailFragment : Fragment() {

    companion object {
        private val TAG = "spot_detail_fragment"
    }

    private lateinit var binding: FragmentSpotDetailBinding
    private val spotDetailViewModel: SpotDetailViewModel by viewModel()
    private var currentSpotDetail: SpotDetail? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpotDetailBinding.inflate(inflater)
        binding.vm = spotDetailViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val spotId = arguments!!.getLong("spotId")
        Log.i(TAG, "args_spot_id: $spotId")
        spotDetailViewModel.setSpotId(spotId)
        val liveData = spotDetailViewModel.getSpotDetail(spotId)
        liveData.observe(viewLifecycleOwner, Observer {
            Log.i(TAG, "spot_detail: $it")
            binding.spotDetail = it
            if (it != null) {
                updateUI(it)
                currentSpotDetail = it
                binding.navigateIcon.alpha = 1F
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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

    /*
    private fun setRatingBar() {
        val ratingBar = binding.ratingBar
        ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                Log.i("home", rating.toString())
            }
    }*/


}