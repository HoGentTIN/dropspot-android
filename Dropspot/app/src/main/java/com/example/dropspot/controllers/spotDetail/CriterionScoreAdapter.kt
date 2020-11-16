package com.example.dropspot.controllers.spotDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dropspot.data.model.dto.CriterionScore
import com.example.dropspot.databinding.ListItemRatingBinding
import com.example.dropspot.viewmodels.SpotDetailViewModel

class CriterionScoreAdapter(private val spotDetailViewModel: SpotDetailViewModel) :
    ListAdapter<CriterionScore, CriterionScoreAdapter.CriterionScoreViewHolder>(
        CriterionScoreDiffCallback()
    ) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CriterionScoreViewHolder {
        return CriterionScoreViewHolder(
            ListItemRatingBinding.inflate(
                LayoutInflater.from(parent.context)
                , parent
                , false
            )
        )
    }

    override fun onBindViewHolder(holder: CriterionScoreViewHolder, position: Int) {
        val criterionScore = getItem(position)
        holder.bind(criterionScore, spotDetailViewModel)
    }

    class CriterionScoreViewHolder(
        private val binding: ListItemRatingBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(criterionScore: CriterionScore, spotDetailViewModel: SpotDetailViewModel) {
            binding.criterionScore = criterionScore
            binding.ratingBar.onRatingBarChangeListener =
                RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                    spotDetailViewModel.vote(criterionScore.criterionId, rating.toDouble())
                }
        }

    }

    class CriterionScoreDiffCallback : DiffUtil.ItemCallback<CriterionScore>() {
        override fun areItemsTheSame(oldItem: CriterionScore, newItem: CriterionScore): Boolean {
            return oldItem.criterionId == newItem.criterionId
        }

        override fun areContentsTheSame(oldItem: CriterionScore, newItem: CriterionScore): Boolean {
            return oldItem == newItem
        }

    }


}



