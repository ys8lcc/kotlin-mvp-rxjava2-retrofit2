package com.yosua.takehome.util

import android.content.Context
import android.widget.ImageView

import com.bumptech.glide.Glide

/**
 * Created by Yosua_Setiawan on 28/07/2017.
 */

object ImageUtil {
    fun loadImageWithGlide(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).placeholder(android.R.color.darker_gray).into(imageView)
    }

    fun loadImageWithGlide(context: Context, drawable: Int, imageView: ImageView) {
        Glide.with(context).load(drawable).placeholder(android.R.color.darker_gray).into(imageView)
    }
}
