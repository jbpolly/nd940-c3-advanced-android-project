package com.udacity.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadInfo(
    val name: String,
    val status: DownloadStatus
): Parcelable
