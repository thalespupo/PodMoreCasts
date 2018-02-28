package com.tapura.podmorecasts.details;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.model.Episode;

import java.util.List;

class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder> {

    private List<Episode> mList;
    private Context mContext;
    private OnDownloadClickListener mCallback;
    public boolean isFavorite;

    public interface OnDownloadClickListener {
        void onDownloadClick(int pos, DownloadListener listener);
    }

    public interface DownloadListener {
        void onDownloadComplete();
        void onDownloadFailed();
    }

    public EpisodesAdapter(Context context, OnDownloadClickListener callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    public EpisodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_episode, parent, false);
        Log.d("THALES", "onCreateViewHolder ");
        return new EpisodeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EpisodeViewHolder holder, int position) {
        //Log.d("THALES", "onBindViewHolder pos:" + position);
        holder.tvTitle.setText(mList.get(position).getTitle());
        if (isFavorite) {
            holder.ivDownload.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public List<Episode> getList() {
        return mList;
    }

    public void setList(List<Episode> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public class EpisodeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, DownloadListener {
        TextView tvTitle;
        ImageView ivDownload;

        public EpisodeViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.text_view_episode_title);
            ivDownload = itemView.findViewById(R.id.image_view_download);

            ivDownload.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ivDownload.setImageResource(R.drawable.ic_file_download_red);
            mCallback.onDownloadClick(getAdapterPosition(), this);

        }

        @Override
        public void onDownloadComplete() {
            ivDownload.setImageResource(R.drawable.ic_done_green);
        }

        @Override
        public void onDownloadFailed() {
            ivDownload.setImageResource(R.drawable.ic_file_download_black);
        }
    }
}
