package com.example.dropspot.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.dropspot.R


@BindingAdapter("rankingImgFromScore")
fun bindRankingImgFromScore(view: ImageView, score: Int) {
    when (score) {
        0 -> view.setImageResource(R.drawable.ic_zero)
        1 -> view.setImageResource(R.drawable.ic_one)
        2 -> view.setImageResource(R.drawable.ic_two)
        3 -> view.setImageResource(R.drawable.ic_three)
        4 -> view.setImageResource(R.drawable.ic_four)
        5 -> view.setImageResource(R.drawable.ic_five)
        else -> view.setImageResource(R.drawable.ic_zero)
    }
}

@BindingAdapter("LikeImgFromLiked")
fun bindLikeImgFromSpotAlreadyLiked(view: ImageView, alreadyLiked: Boolean) {
    if (alreadyLiked) {
        view.setImageResource(R.drawable.ic_like_filled)
    } else {
        view.setImageResource(R.drawable.ic_like_outlined)
    }
}


