package com.example.dropspot.controllers.spotDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dropspot.data.model.dto.CriterionScore
import com.example.dropspot.databinding.ListItemRatingBinding

class CriterionScoreAdapter :
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
        holder.bind(criterionScore)
    }

    class CriterionScoreViewHolder(
        private val binding: ListItemRatingBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(criterionScore: CriterionScore) {
            binding.criterionScore = criterionScore
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



