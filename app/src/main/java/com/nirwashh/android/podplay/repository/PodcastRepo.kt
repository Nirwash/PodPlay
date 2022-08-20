package com.nirwashh.android.podplay.repository

import androidx.lifecycle.LiveData
import com.nirwashh.android.podplay.db.PodcastDao
import com.nirwashh.android.podplay.model.Episode
import com.nirwashh.android.podplay.model.Podcast
import com.nirwashh.android.podplay.service.RssFeedResponse
import com.nirwashh.android.podplay.service.RssFeedService
import com.nirwashh.android.podplay.util.DateUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PodcastRepo(private var feedService: RssFeedService, private var podcastDao: PodcastDao) {

    private fun rssItemsToEpisodes(
        episodeResponse: List<RssFeedResponse.EpisodeResponse>
    ): List<Episode> {
        return episodeResponse.map {
            Episode(
                it.guid ?: "",
                null,
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
        return Podcast(
            null,
            feedUrl,
            rssResponse.title,
            description,
            imageUrl,
            rssResponse.lastUpdated,
            episodes = rssItemsToEpisodes(items)
        )
    }


    suspend fun getPodcast(feedUrl: String): Podcast? {
        val podcastLocal = podcastDao.loadPodcast(feedUrl)
        if (podcastLocal != null) {
            podcastLocal.id?.let {
                podcastLocal.episodes = podcastDao.loadEpisodes(it)
                return podcastLocal
            }
        }
        var podcast: Podcast? = null
        val feedResponse = feedService.getFeed(feedUrl)
        if (feedResponse != null) {
            podcast = rssResponseToPodcast(feedUrl, "", feedResponse)
        }
        return podcast
    }

    fun save(podcast: Podcast) {
        GlobalScope.launch {
            val podcastId = podcastDao.insertPodcast(podcast)
            for (episode in podcast.episodes) {
                episode.podcastId = podcastId
                podcastDao.insertEpisode(episode)
            }
        }
    }

    fun delete(podcast: Podcast) {
        GlobalScope.launch {
            podcastDao.deletePodcast(podcast)
        }
    }

    fun getAll(): LiveData<List<Podcast>> {
        return podcastDao.loadPodcasts()
    }

}