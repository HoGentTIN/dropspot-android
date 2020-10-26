package com.example.dropspot.ui.spot

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.fragment.app.Fragment
import com.example.dropspot.databinding.FragmentSpotDetailBinding

class SpotDetailFragment : Fragment() {

    private lateinit var binding: FragmentSpotDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpotDetailBinding.inflate(inflater)
        return binding.root
    }

    private fun setupUI() {
        adjustScreenToOrientation()
        setRatingBar()
    }

    private fun setRatingBar() {
        val ratingBar = binding.ratingBar
        ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                Log.i("home", rating.toString())
            }
    }

    private fun adjustScreenToOrientation() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.descriptionNestedScrollView.visibility = View.GONE
            binding.spotPhoto.visibility = View.GONE
            binding.spotNameTextv.gravity = Gravity.CENTER
            binding.creatorTextv.gravity = Gravity.CENTER

        }
    }

}