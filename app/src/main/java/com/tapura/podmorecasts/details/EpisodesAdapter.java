package com.tapura.podmorecasts.details;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.model.Episode;

import java.util.List;

class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder> {

    private List<Episode> mList;

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
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0 ;
    }

    public List<Episode> getList() {
        return mList;
    }

    public void setList(List<Episode> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public class EpisodeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        public EpisodeViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.text_view_episode_title);
        }
    }
}
