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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.model.DownloadInfo
import com.udacity.model.DownloadOption
import com.udacity.model.DownloadStatus
import com.udacity.notification.sendStatusNotification
import java.util.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var binding: ActivityMainBinding
    private var startTime = 0L
    lateinit var downloadManager: DownloadManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.main_title)
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        setupLayout()


    }

    private fun setupLayout() {

        binding.content.glideOption.text = DownloadOption.GLIDE.getDescription(this)
        binding.content.loadAppOption.text = DownloadOption.LOAD_APP.getDescription(this)
        binding.content.retrofitOption.text = DownloadOption.RETROFIT.getDescription(this)

        binding.content.customButton.setOnClickListener {
            download(getSelectedDownload())
        }


    }

    private fun getSelectedDownload(): DownloadOption {
        return when(binding.content.radioGroup.checkedRadioButtonId){
            R.id.glide_option -> DownloadOption.GLIDE
            R.id.load_app_option -> DownloadOption.LOAD_APP
            else -> DownloadOption.RETROFIT
        }
    }

    private fun getDownloadUrl(): String {
        return when(binding.content.radioGroup.checkedRadioButtonId){
            R.id.glide_option -> DownloadOption.GLIDE.getUrl(this)
            R.id.load_app_option -> DownloadOption.LOAD_APP.getUrl(this)
            else -> DownloadOption.RETROFIT.getUrl(this)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var downloadInfo: DownloadInfo? = null
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                val deltaTime =  System.currentTimeMillis() - startTime
                val newTime = standardAnimTime - deltaTime
                if(binding.content.customButton.set.isRunning){
                    binding.content.customButton.cancelAnimation()
                    binding.content.customButton.startDownload(newTime)
                }
                val q = DownloadManager.Query()
                q.setFilterById(id)
                val c: Cursor = downloadManager.query(q)
                if (c.moveToFirst()) {
                    val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloadInfo = DownloadInfo(title, DownloadStatus.SUCCESS)
                    }else if(status == DownloadManager.STATUS_FAILED){
                        downloadInfo = DownloadInfo(title, DownloadStatus.FAILED)
                    }
                }
                val notificationManager = this@MainActivity.getSystemService(NotificationManager::class.java)
                downloadInfo?.let {
                    notificationManager.sendStatusNotification(applicationContext, downloadInfo)
                }
                Toast.makeText(applicationContext, getString(R.string.download_completed) , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun download(option: DownloadOption) {
        val request =
            DownloadManager.Request(Uri.parse(option.getUrl(this)))
                .setTitle(option.getDescription(this))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        startTime = System.currentTimeMillis()
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        binding.content.customButton.startDownload()
    }



}
