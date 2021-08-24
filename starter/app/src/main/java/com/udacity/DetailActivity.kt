package com.udacity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.databinding.ActivityMainBinding
import com.udacity.model.DownloadInfo
import com.udacity.model.DownloadStatus
import com.udacity.notification.NOTIFICATION_INFO_KEY


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var downloadInfo: DownloadInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.details_title)

        if(intent != null && intent.hasExtra(NOTIFICATION_INFO_KEY)){
            downloadInfo = intent.getParcelableExtra(NOTIFICATION_INFO_KEY) as? DownloadInfo
            Log.d("Details", "Info - Title = ${downloadInfo?.name} - Status: ${downloadInfo?.status}")
        }
       setupDownloadInfo(downloadInfo)

        binding.detailsContent.detailsOkButton.setOnClickListener {
            onBackPressed()
        }

    }

    private fun setupDownloadInfo(downloadInfo: DownloadInfo?) {
        downloadInfo?.let {info->
            binding.detailsContent.fileNameText.text = info.name
            binding.detailsContent.statusDescription.text = info.status.text
            when(info.status){
                DownloadStatus.SUCCESS ->{
                    binding.detailsContent.statusDescription.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
                }
                else ->{
                    binding.detailsContent.statusDescription.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                }
            }
        }
    }


}
