package com.example.utils

import android.view.View
import android.view.animation.AlphaAnimation

object AnimationUtil {
    fun fadeOutView(view: View, durationMs: Long = 500) {
        if (view.visibility == View.GONE) return
        val anim = AlphaAnimation(1f, 0f).apply {
            duration = durationMs
            fillAfter = false
        }
        view.startAnimation(anim)
        view.visibility = View.GONE
    }
    
    fun fadeInView(view: View, durationMs: Long = 300) {
        if (view.visibility == View.VISIBLE) return
        val anim = AlphaAnimation(0f, 1f).apply {
            duration = durationMs
            fillAfter = true
        }
        view.startAnimation(anim)
        view.visibility = View.VISIBLE
    }
}
