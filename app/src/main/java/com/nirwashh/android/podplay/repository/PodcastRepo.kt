package com.nirwashh.android.podplay.repository

import com.nirwashh.android.podplay.model.Podcast

class PodcastRepo {

    fun getPodcast(feedUrl: String): Podcast? {
        return Podcast(feedUrl, "No name", "No description", "No image")
    }
}