package com.nirwashh.android.podplay.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.nirwashh.android.podplay.databinding.ActivityPodcastBinding
import com.nirwashh.android.podplay.repository.ItunesRepo
import com.nirwashh.android.podplay.service.ItunesService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PodcastActivity : AppCompatActivity() {
    private lateinit var b: ActivityPodcastBinding
    val TAG = javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPodcastBinding.inflate(layoutInflater)
        setContentView(b.root)
        val itunesService = ItunesService.instance
        val itunesRepo = ItunesRepo(itunesService)

        GlobalScope.launch {
            val result = itunesRepo.searchByTerm("Android Developer")
            Log.d(TAG, "Results = ${result.body()}")
        }
    }
}