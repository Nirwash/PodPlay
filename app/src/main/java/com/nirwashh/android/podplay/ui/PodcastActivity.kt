package com.nirwashh.android.podplay.ui

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import com.nirwashh.android.podplay.repository.PodcastRepo
import com.nirwashh.android.podplay.service.ItunesService
import com.nirwashh.android.podplay.viewmodel.PodcastViewModel
import com.nirwashh.android.podplay.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PodcastActivity : AppCompatActivity(), PodcastListAdapterListener {
    private lateinit var b: ActivityPodcastBinding
    private lateinit var podcastListAdapter: PodcastListAdapter
    private lateinit var searchMenuItem: MenuItem
    private val searchViewModel by viewModels<SearchViewModel>()
    private val podcastViewModel by viewModels<PodcastViewModel>()
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
        searchMenuItem = menu.findItem(R.id.search_item)
        val searchView = searchMenuItem.actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        if (b.podcastRecyclerView.visibility == View.INVISIBLE) searchMenuItem.isVisible = false
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
        podcastViewModel.podcastRepo = PodcastRepo()
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
        val feedUrl = podcastSummaryViewData.feedUrl ?: return
        showProgressBar()
        val podcast = podcastViewModel.getPodcast(podcastSummaryViewData)
        hideProgressBar()
        if (podcast != null) {
            showDetailsFragment()
        } else {
            showError("Error loading feed $feedUrl")
        }
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok_button), null)
            .create()
            .show()
    }

    private fun showProgressBar() {
        b.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        b.progressBar.visibility = View.INVISIBLE
    }

    companion object {
        private const val TAG_DETAILS_FRAGMENT = "DetailsFragment"
    }

    private fun createPodcastDetailFragment(): PodcastDetailFragment {
        var podcastDetailFragment =
            supportFragmentManager.findFragmentByTag(TAG_DETAILS_FRAGMENT) as
                    PodcastDetailFragment?
        if (podcastDetailFragment == null) {
            podcastDetailFragment = PodcastDetailFragment.newInstance()
        }
        return podcastDetailFragment
    }

    private fun showDetailsFragment() {
        val podcastDetailFragment = createPodcastDetailFragment()
        supportFragmentManager.beginTransaction().add(
            R.id.podcastDetailContainer,
            podcastDetailFragment,
            TAG_DETAILS_FRAGMENT
        ).addToBackStack("DetailsFragment").commit()
        b.podcastRecyclerView.visibility = View.INVISIBLE
        searchMenuItem.isVisible = false
    }
}