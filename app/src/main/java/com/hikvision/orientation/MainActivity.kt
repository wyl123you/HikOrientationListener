package com.hikvision.orientation

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.provider.Settings.System
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.namespace.R

@SuppressLint("SourceLockedOrientationActivity")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        HikOrientationListener(this) { last, current ->
            title = "$last   $current"
            val rotationEnabled = System.getInt(contentResolver, System.ACCELEROMETER_ROTATION) == 1

            if (rotationEnabled) {
                when (current) {
                    0 -> {
                        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        }
                        doRotation(current)
                    }
                    90 -> {
                        when (last) {
                            0 -> doRotation(current)
                            180 -> doRotation(-current)
                            else -> if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                                doRotation(90)
                            } else {
                                doRotation(-current)
                            }
                        }
                    }
                    180 -> {
                        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                        }
                        doRotation(0)
                    }
                    270 -> {
                        when (last) {
                            0 -> doRotation(current)
                            180 -> doRotation(90)
                            else -> if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                                doRotation(-current)
                            } else {
                                doRotation(90)
                            }
                        }
                    }
                }
            } else {
                doRotation(current)
            }
        }
    }


    private fun doRotation(rotation: Int) {
        findViewById<ImageView>(R.id.icon).hikRotate(rotation)
    }

}