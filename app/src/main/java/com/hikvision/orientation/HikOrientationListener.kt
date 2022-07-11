package com.hikvision.orientation

import android.content.Context
import android.util.Log
import android.util.Range
import android.view.OrientationEventListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class HikOrientationListener(
    context: Context,
    dividerScope: Int = 30,
    private val onOrientationChangedListener: ((last: Int, current: Int) -> Unit)? = null

) : OrientationEventListener(context) {

    companion object {
        const val TAG = "HikOrientationListener"
    }

    private val offset = dividerScope shr 1

    //正向竖屏（由两个范围组成）
    private val portraitRange1: Range<Int> = Range(315 + offset, 360)
    private val portraitRange2: Range<Int> = Range(0, 45 - offset)

    //右横屏
    private val rightRange: Range<Int> = Range(45 + offset, 135 - offset)

    //反向竖屏
    private val reservePortrait: Range<Int> = Range(135 + offset, 225 - offset)

    //左横屏
    private val leftRange: Range<Int> = Range(225 + offset, 315 - offset)

    private var lastOrientation: Int = 0

    init {
        Log.d(TAG, "portraitRange1: $portraitRange1")
        Log.d(TAG, "portraitRange2: $portraitRange2")
        Log.d(TAG, "rightRange: $rightRange")
        Log.d(TAG, "reservePortrait: $reservePortrait")
        Log.d(TAG, "leftRange: $leftRange")

        val hikOrientationListenerObserver = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                Log.d(TAG, "启用方向监听")
                this@HikOrientationListener.enable()
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                Log.d(TAG, "禁用方向监听")
                this@HikOrientationListener.disable()
            }
        }
        val activity = context as AppCompatActivity
        activity.lifecycle.addObserver(hikOrientationListenerObserver)
    }

    override
    fun onOrientationChanged(orientation: Int) {
        //Log.d(TAG, "orientation: $orientation")
        if (isSameAsLast(orientation)) return

        val des = getDesByOrientation(lastOrientation)
        when (orientation) {
            in portraitRange1, in portraitRange2 -> {
                //正向竖屏
                Log.d(TAG, "onOrientationChanged: $des 转向 正向竖屏")
                onOrientationChangedListener?.invoke(lastOrientation, 0)
            }
            in rightRange -> {
                //右横屏
                Log.d(TAG, "onOrientationChanged: $des 转向 右横屏")
                onOrientationChangedListener?.invoke(lastOrientation, 90)
            }
            in reservePortrait -> {
                //反向竖屏
                Log.d(TAG, "onOrientationChanged: $des 转向 反向竖屏")
                onOrientationChangedListener?.invoke(lastOrientation, 180)
            }
            in leftRange -> {
                //左横屏
                Log.d(TAG, "onOrientationChanged: $des 转向 左横屏")
                onOrientationChangedListener?.invoke(lastOrientation, 270)
            }
        }
        lastOrientation = convertOrientation(orientation)
    }

    private fun isSameAsLast(orientation: Int): Boolean {
        return when (orientation) {
            in portraitRange1, in portraitRange2 -> {
                (lastOrientation in portraitRange1) || (lastOrientation in portraitRange2)
            }
            in rightRange -> {
                lastOrientation in rightRange
            }
            in reservePortrait -> {
                lastOrientation in reservePortrait
            }
            in leftRange -> {
                lastOrientation in leftRange
            }
            else -> false
        }
    }

    private fun convertOrientation(orientation: Int): Int {
        return when (orientation) {
            in portraitRange1, in portraitRange2 -> 0
            in rightRange -> 90
            in reservePortrait -> 180
            in leftRange -> 270
            else -> lastOrientation
        }
    }

    private fun getDesByOrientation(orientation: Int): String {
        return when (orientation) {
            in portraitRange1, in portraitRange2 -> "正向竖屏"
            in rightRange -> "右横屏"
            in reservePortrait -> "反向竖屏"
            in leftRange -> "左横屏"
            else -> "水平"
        }
    }
}