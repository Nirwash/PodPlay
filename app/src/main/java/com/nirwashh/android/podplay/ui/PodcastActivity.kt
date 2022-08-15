package com.nirwashh.android.podplay.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nirwashh.android.podplay.R
import com.nirwashh.android.podplay.adapter.PodcastListAdapter
import com.nirwashh.android.podplay.adapter.PodcastListAdapter.*
import com.nirwashh.android.podplay.databinding.ActivityPodcastBinding
import com.nirwashh.android.podplay.repository.ItunesRepo
import com.nirwashh.android.podplay.service.ItunesService
import com.nirwashh.android.podplay.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PodcastActivity : AppCompatActivity(), PodcastListAdapterListener {
    private lateinit var b: ActivityPodcastBinding
    private val searchViewModel by viewModels<SearchViewModel>()
    private lateinit var podcastListAdapter: PodcastListAdapter
    val TAG = javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPodcastBinding.inflate(layoutInflater)
        setContentView(b.root)

        setupToolbar()
        setupViewModels()
        updateControls()
        handleIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)
        val searchMenuItem = menu?.findItem(R.id.search_item)
        val searchView = searchMenuItem?.actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        return true
    }

    private fun performSearch(term: String) {
        showProgressBar()
        GlobalScope.launch {
            val results = searchViewModel.searchPodcast(term)
            withContext(Dispatchers.Main) {
                hideProgressBar()
                b.toolbar.title = term
                podcastListAdapter.setSearchData(results)
            }
        }
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY) ?: return
            performSearch(query)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun setupToolbar() {
        setSupportActionBar(b.toolbar)
    }

    private fun setupViewModels() {
        val service = ItunesService.instance
        searchViewModel.iTunesRepo = ItunesRepo(service)
    }

    private fun updateControls() {
        b.podcastRecyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        b.podcastRecyclerView.layoutManager = layoutManager

        val dividerItemDecoration =
            DividerItemDecoration(b.podcastRecyclerView.context, layoutManager.orientation)

        b.podcastRecyclerView.addItemDecoration(dividerItemDecoration)
        podcastListAdapter = PodcastListAdapter(null, this, this)
        b.podcastRecyclerView.adapter = podcastListAdapter
    }

    override fun onShowDetails(podcastSummaryViewData: SearchViewModel.PodcastSummaryViewData) {
        //Not implemented yet
    }

    private fun showProgressBar() {
        b.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        b.progressBar.visibility = View.INVISIBLE
    }
}