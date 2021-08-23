package com.udacity.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.DetailActivity
import com.udacity.R
import com.udacity.model.DownloadInfo

private const val NOTIFICATION_ID = 0
private const val NOTIFICATION_INFO_KEY = "notification_info"

fun NotificationManager.sendStatusNotification(
    messageBody: String,
    applicationContext: Context,
    downloadInfo: DownloadInfo
) {
    createStatusChannel(applicationContext)

    val contentIntent = Intent(applicationContext, DetailActivity::class.java).putExtra(NOTIFICATION_INFO_KEY, downloadInfo)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT //specifies the option to create a new pending intent or update an existing one
    )

//    val bigPicStyle = NotificationCompat.BigPictureStyle()
//        .bigPicture(eggImage)
//        .bigLargeIcon(null) //so the large icon goes away when expanded


    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.default_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
//        .setStyle(bigPicStyle)
//        .setLargeIcon(eggImage)
        .addAction(
            R.drawable.ic_baseline_text_snippet_24,
            applicationContext.getString(R.string.check_status),
            contentPendingIntent
        )
        //for api 25 or lower
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(NOTIFICATION_ID, builder.build())
}

private fun NotificationManager.createStatusChannel(applicationContext: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            applicationContext.getString(R.string.default_notification_channel_id),
            applicationContext.getString(R.string.default_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            lightColor = Color.BLUE
            enableVibration(true)
            description = applicationContext.getString(R.string.notification_channel_description)
            setShowBadge(false)
        }
        createNotificationChannel(notificationChannel)
    }
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}