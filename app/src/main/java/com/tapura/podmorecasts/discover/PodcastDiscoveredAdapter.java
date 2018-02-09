package com.tapura.podmorecasts.discover;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.model.ItunesResultsItem;

import java.util.List;

class PodcastDiscoveredAdapter extends RecyclerView.Adapter<PodcastDiscoveredAdapter.DiscoveredViewHolder> {

    private List<ItunesResultsItem> mList;
    private Context mContext;

    public interface PodcastDiscoveredOnClickListener {
        void onClick(int pos);
    }

    private PodcastDiscoveredOnClickListener mCallback;

    public PodcastDiscoveredAdapter(Context context, PodcastDiscoveredOnClickListener listener) {
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
        ItunesResultsItem item = mList.get(position);
        String stringImage = item.getArtworkUrl600();
        Picasso.with(mContext)
                .load(stringImage)
                .into(holder.ivThumbnail);

        holder.tvPodcastName.setText(item.getCollectionName());
        holder.tvPodcastAuthorName.setText(item.getArtistName());
        holder.tvPodcastGenre.setText(item.getPrimaryGenreName());

    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setPodcastList(List<ItunesResultsItem> podcastList) {
        this.mList = podcastList;
        notifyDataSetChanged();
    }

    public List<ItunesResultsItem> getList() {
        return mList;
    }


    public class DiscoveredViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivThumbnail;
        TextView tvPodcastName;
        TextView tvPodcastAuthorName;
        TextView tvPodcastGenre;

        public DiscoveredViewHolder(View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.image_view_podcast_item);
            tvPodcastName = itemView.findViewById(R.id.text_view_podcast_name);
            tvPodcastAuthorName = itemView.findViewById(R.id.text_view_podcast_author_name);
            tvPodcastGenre = itemView.findViewById(R.id.text_view_podcast_genre);
        }

        @Override
        public void onClick(View v) {
            mCallback.onClick(getAdapterPosition());
        }
    }
}
