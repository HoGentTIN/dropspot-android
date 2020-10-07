package com.example.dropspot.utils

import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.view.ViewCompat

object Anims {
    fun rotateForward(view: View) {
        ViewCompat.animate(view)
                .rotation(135.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(OvershootInterpolator(10.0F))
                .start()
    }

    fun rotateBackward(view: View) {
        ViewCompat.animate(view)
                .rotation(0.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(OvershootInterpolator(10.0F))
                .start()
    }
}
