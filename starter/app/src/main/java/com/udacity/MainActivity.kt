package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.udacity.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var startTime = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.content.customButton.setOnClickListener {
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                val deltaTime =  System.currentTimeMillis() - startTime
                val newTime = standardAnimTime - deltaTime
                if(binding.content.customButton.set.isRunning){
                    binding.content.customButton.cancelAnimation()
                    binding.content.customButton.startDownload(newTime)
                }
                Toast.makeText(applicationContext, "Download Completed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        startTime = System.currentTimeMillis()
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        binding.content.customButton.startDownload()

//        reportDownloadStatus(downloadManager)
    }

//    fun reportDownloadStatus(downloadManager: DownloadManager) {
//        lifecycleScope.launch {
//            var finishedDownload = false
//            var progress = 0
//            while (!finishedDownload) {
//                val cursor: Cursor =
//                    downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
//                if (cursor.moveToFirst()) {
//                    when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
//                        DownloadManager.STATUS_FAILED -> {
//                            finishedDownload = true
//                        }
//                        DownloadManager.STATUS_PAUSED -> {
//                        }
//                        DownloadManager.STATUS_PENDING -> {
//                        }
//                        DownloadManager.STATUS_RUNNING -> {
//                            val total: Long = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
//                            if (total >= 0) {
//                                val downloaded: Long =
//                                    cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
//                                publishProgress((downloaded * 100L / total).toInt())
//                            }
//                        }
//                        DownloadManager.STATUS_SUCCESSFUL -> {
//                            publishProgress(100)
//                            finishedDownload = true
//                            Toast.makeText(
//                                this@MainActivity,
//                                "Download Completed",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                }
//            }
//        }
//    }

    companion object {
        //        private const val URL =
//            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL = "https://github.com/bumptech/glide"
        private const val CHANNEL_ID = "channelId"
    }

}
