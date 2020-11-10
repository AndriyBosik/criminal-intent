package com.house.criminalintent.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class PictureUtils {

    companion object {
        fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {
            var options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, options)

            val srcWidth = options.outWidth.toFloat()
            val srcHeight = options.outHeight.toFloat()

            val inSampleSize: Int = if (srcHeight > destHeight || srcWidth > destWidth) {
                val heightScale = srcHeight / destHeight
                val widthScale = srcWidth / destWidth
                (if (heightScale > widthScale) heightScale else widthScale).roundToInt()
            } else {
                1
            }
            options = BitmapFactory.Options()
            options.inSampleSize = inSampleSize

            return BitmapFactory.decodeFile(path, options)
        }

        fun getScaledBitmap(path: String, activity: Activity): Bitmap {
            val size = Point()
            activity.windowManager.defaultDisplay.getSize(size)
            return getScaledBitmap(path, size.x, size. y)
        }
    }

}