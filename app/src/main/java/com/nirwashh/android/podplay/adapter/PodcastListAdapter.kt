package com.nirwashh.android.podplay.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nirwashh.android.podplay.R
import com.nirwashh.android.podplay.databinding.SearchItemBinding
import com.nirwashh.android.podplay.viewmodel.SearchViewModel.*
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class PodcastListAdapter(
    private var podcastSummaryViewList: List<PodcastSummaryViewData>?,
    private val podcastListAdapterListener: PodcastListAdapterListener,
    private val parentActivity: Activity
) : RecyclerView.Adapter<PodcastListAdapter.ViewHolder>() {

    interface PodcastListAdapterListener {
        fun onShowDetails(podcastSummaryViewData: PodcastSummaryViewData)
    }

    inner class ViewHolder(
        val binding: SearchItemBinding,
        private val podcastListAdapterListener: PodcastListAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {
        var podcastSummaryViewData: PodcastSummaryViewData? = null
        init {
            binding.searchItem.setOnClickListener {
                podcastSummaryViewData?.let {
                    podcastListAdapterListener.onShowDetails(it)
                }
            }
        }
    }

    fun setSearchData(podcastSummaryViewData: List<PodcastSummaryViewData>) {
        podcastSummaryViewList = podcastSummaryViewData
        this.notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastListAdapter.ViewHolder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, podcastListAdapterListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchViewList = podcastSummaryViewList ?: return
        val searchView = searchViewList[position]
        with(holder) {
            podcastSummaryViewData = searchView
            binding.podcastNameTextView.text = searchView.name
            binding.podcastLastUpdatedTextView.text = searchView.lastUpdated
        }
        val url = searchView.imageUrl
        Glide.with(parentActivity).load(url).into(holder.binding.podcastImage)
    }

    override fun getItemCount() = podcastSummaryViewList?.size ?: 0

}