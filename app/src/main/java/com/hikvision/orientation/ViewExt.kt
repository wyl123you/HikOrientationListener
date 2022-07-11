package com.hikvision.orientation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

fun View.hikRotate(degree: Int) {
    //degree为屏幕旋转的角度，与View需要旋转的角度相反，需转化
    val finalEnd = when (degree) {
        0, 180 -> degree.toFloat()
        90 -> 270f
        else -> 90f
    }

    val start = this.rotation
    val end = if (start == 0f && finalEnd == 270f) {
        -90f
    }  else if (start == 270f) {
        when (finalEnd) {
            0f -> 360f
            90f -> 450f
            else -> finalEnd
        }
    }else if (start == 180f && finalEnd == 0f) {
        360f
    } else {
        finalEnd
    }
    val rotateAnimator = ValueAnimator.ofFloat(start, end).apply {
        duration = 300
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener {
            val value = it.animatedValue as Float
            this@hikRotate.rotation = value
        }
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                this@hikRotate.rotation = finalEnd
            }
        })
    }
    rotateAnimator.start()
}