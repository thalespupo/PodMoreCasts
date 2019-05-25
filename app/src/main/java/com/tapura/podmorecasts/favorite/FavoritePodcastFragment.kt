package com.tapura.podmorecasts.favorite

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.tapura.podmorecasts.R
import kotlinx.android.synthetic.main.content_favorite_podcast.*

class FavoritePodcastFragment : Fragment(), PodcastFavoriteAdapter.PodcastFavoriteOnClickListener {

    companion object {
        const val FRAG_TAG = "favorite"
    }

    private val adapter by lazy { PodcastFavoriteAdapter(context, this) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_favorite_podcast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGridView(view)
        setupViewModel()
    }

    private fun setupGridView(view: View) {
        view.findViewById<RecyclerView>(R.id.recycler_view_podcasts_favorite_list)?.apply {
            adapter = this@FavoritePodcastFragment.adapter
            layoutManager = GridLayoutManager(context, 3)
            setHasFixedSize(true)
        }
    }

    private fun setupViewModel() {
        val model = FavoritePodcastViewModel()
        model.loadPodcasts()

        model.loading.observe(this, Observer { loading(it) })
        model.podcastList.observe(this, Observer { adapter.setPodcastList(it) })
        model.error.observe(this, Observer { showError() })
    }

    interface FavoriteClickListener {
        fun onFavoritePodcastClick(feed: String)
    }

    override fun onClick(pos: Int) {
        activity?.let {
            val feedUrl = adapter.list[pos].feedUrl
            (it as FavoriteClickListener).onFavoritePodcastClick(feedUrl)
        }
    }

    private fun showError() {
        Toast.makeText(activity, getString(R.string.toast_list_not_loaded), Toast.LENGTH_SHORT).show()
    }

    private fun loading(status: Boolean?) {
        status?.let {
            layout_loading_progressbar?.let {
                if (status) it.visibility = View.VISIBLE else it.visibility = View.GONE
            }
        }
    }
}
