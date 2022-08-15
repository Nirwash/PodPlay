package com.nirwashh.android.podplay.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.nirwashh.android.podplay.R
import com.nirwashh.android.podplay.databinding.FragmentPodcastDetailBinding
import com.nirwashh.android.podplay.viewmodel.PodcastViewModel

class PodcastDetailFragment : Fragment() {
    private lateinit var dataBinding: FragmentPodcastDetailBinding
    private val podcastViewModel: PodcastViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentPodcastDetailBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateControls()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_detail, menu)
    }

    private fun updateControls() {
        val viewData = podcastViewModel.activePodcastViewData ?: return
        dataBinding.feedTitleTextView.text = viewData.feedTitle
        dataBinding.feedDescTextView.text = viewData.feedDesc
        activity?.let { activity ->
            Glide.with(activity).load(viewData.imageUrl).into(dataBinding.feedImageView)
        }
    }

    companion object {
        fun newInstance(): PodcastDetailFragment {
            return PodcastDetailFragment()
        }
    }
}