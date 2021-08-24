package com.udacity.model

import android.content.Context
import com.udacity.R

enum class DownloadOption {

    GLIDE,
    LOAD_APP,
    RETROFIT;

    fun getUrl(context: Context): String{
        return when(this){
            GLIDE -> context.resources.getString(R.string.glide_url)
            LOAD_APP -> context.resources.getString(R.string.load_app_url)
            RETROFIT -> context.resources.getString(R.string.retrofit_url)
        }
    }

    fun getDescription(context: Context): String{
        return when(this){
            GLIDE -> context.resources.getString(R.string.glide)
            LOAD_APP -> context.resources.getString(R.string.load_app)
            RETROFIT -> context.resources.getString(R.string.retrofit)
        }
    }


}