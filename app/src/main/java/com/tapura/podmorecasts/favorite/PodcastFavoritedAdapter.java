package com.tapura.podmorecasts.favorite;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.model.Podcast;

import java.util.List;

class PodcastFavoritedAdapter extends RecyclerView.Adapter<PodcastFavoritedAdapter.DiscoveredViewHolder> {

    private List<Podcast> mList;
    private Context mContext;

    public interface PodcastFavoritedOnClickListener {
        void onClick(int pos);
    }

    private PodcastFavoritedOnClickListener mCallback;

    public PodcastFavoritedAdapter(Context context, PodcastFavoritedOnClickListener listener) {
        mContext = context;
        mCallback = listener;
    }

    @Override
    public DiscoveredViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_podcast, parent, false);
        return new DiscoveredViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DiscoveredViewHolder holder, int position) {
        Podcast item = mList.get(position);
        String stringImage = item.getThumbnailPath();
        Picasso.with(mContext)
                .load(stringImage)
                .placeholder(mContext.getDrawable(R.drawable.ic_headset_black))
                .into(holder.ivThumbnail);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setPodcastList(List<Podcast> podcastList) {
        this.mList = podcastList;
        notifyDataSetChanged();
    }

    public List<Podcast> getList() {
        return mList;
    }


    public class DiscoveredViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivThumbnail;

        public DiscoveredViewHolder(View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.image_view_podcast_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCallback.onClick(getAdapterPosition());
        }
    }
}
