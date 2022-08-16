package com.nirwashh.android.podplay.repository

import com.nirwashh.android.podplay.model.Episode
import com.nirwashh.android.podplay.model.Podcast
import com.nirwashh.android.podplay.service.RssFeedResponse
import com.nirwashh.android.podplay.service.RssFeedService
import com.nirwashh.android.podplay.util.DateUtils

class PodcastRepo(private var feedService: RssFeedService) {

    private fun rssItemsToEpisodes(
        episodeResponse: List<RssFeedResponse.EpisodeResponse>
    ): List<Episode> {
        return episodeResponse.map {
            Episode(
                it.guid ?: "",
                it.title ?: "",
                it.description ?: "",
                it.url ?: "",
                it.type ?: "",
                DateUtils.xmlDateToDate(it.pubDate),
                it.duration ?: ""
            )
        }
    }

    private fun rssResponseToPodcast(
        feedUrl: String, imageUrl: String, rssResponse: RssFeedResponse
    ): Podcast? {
        val items = rssResponse.episodes ?: return null
        val description = if (rssResponse.description == "")
            rssResponse.summary else rssResponse.description
        return Podcast(feedUrl, rssResponse.title, description, imageUrl, rssResponse.lastUpdated, episodes = rssItemsToEpisodes(items))
    }


    suspend fun getPodcast(feedUrl: String): Podcast? {
        var podcast: Podcast? = null
        val feedResponse = feedService.getFeed(feedUrl)
        if (feedResponse != null) {
            podcast = rssResponseToPodcast(feedUrl, "", feedResponse)
        }
        return podcast

    }
}