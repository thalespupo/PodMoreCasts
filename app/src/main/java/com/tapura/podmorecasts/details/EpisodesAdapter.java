package com.tapura.podmorecasts.details;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tapura.podmorecasts.MyLog;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.model.Episode;
import com.tapura.podmorecasts.model.EpisodeMediaState;

import java.util.List;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder> {

    private List<Episode> mList;
    private final OnDownloadClickListener mCallback;
    public boolean isFavorite;

    public interface OnDownloadClickListener {
        void onDownloadClick(int pos);
    }

    public EpisodesAdapter(OnDownloadClickListener callback) {
        mCallback = callback;
    }

    @Override
    public EpisodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_episode, parent, false);
        MyLog.d(this.getClass(), "onCreateViewHolder ");
        return new EpisodeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EpisodeViewHolder holder, int position) {
        holder.tvTitle.setText(mList.get(position).getTitle());
        if (isFavorite) {
            holder.ivDownload.setVisibility(View.VISIBLE);
            Episode epi = mList.get(position);
            holder.ivDownload.setImageResource(holder.getDownloadIcon(epi.getEpisodeState()));
        } else {
            holder.ivDownload.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setList(List<Episode> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public class EpisodeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView tvTitle;
        final ImageView ivDownload;

        public EpisodeViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.text_view_episode_title);
            ivDownload = itemView.findViewById(R.id.image_view_download);
            ivDownload.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            MyLog.d(this.getClass(), "onClick: ");
            mCallback.onDownloadClick(getAdapterPosition());
        }

        private int getDownloadIcon(EpisodeMediaState episodeState) {
            switch (episodeState) {
                case COMPLETED:
                    return R.drawable.ic_done_green;
                case DOWNLOADING:
                    return R.drawable.ic_file_download_red;
                case NOT_IN_DISK:
                    return R.drawable.ic_file_download_black;
                default:
                    return -1;
            }
        }
    }
}
