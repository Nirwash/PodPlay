package com.nirwashh.android.podplay.repository

import com.nirwashh.android.podplay.service.ItunesService

class ItunesRepo(private val itunesService: ItunesService) {

    suspend fun searchByTerm(term: String) = itunesService.searchPodcastByTerm(term)
}
