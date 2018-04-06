package com.tapura.podmorecasts.widget;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tapura.podmorecasts.MyLog;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.database.FirebaseDb;
import com.tapura.podmorecasts.model.Podcast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        MyLog.d(getClass(), "onGetViewFactory");

        return new PodcastRemoteViewsFactory();
    }

    private class PodcastRemoteViewsFactory implements RemoteViewsFactory {
        private Context mContext;

        public PodcastRemoteViewsFactory() {
            MyLog.d(getClass(), "PodcastRemoteViewsFactory");
            mContext = getApplicationContext();
        }

        @Override
        public void onCreate() {
            MyLog.d(getClass(), "onCreate");
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
            MyLog.d(getClass(), "onDestroy");
        }

        @Override
        public int getCount() {
            MyLog.d(getClass(), "getCount");
            return mPodcastList == null ? 0 : mPodcastList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            MyLog.d(getClass(), "getViewAt");
            if (getCount() == 0) {
                return null;
            }
            Podcast podcast = mPodcastList.get(position);

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.podcast_widget_list_item);

            try {
                Bitmap b = Picasso.with(mContext).load(podcast.getImagePath()).get();
                views.setImageViewBitmap(R.id.image_view_podcast_item, b);
            } catch (IOException e) {
                e.printStackTrace();
            }

            views.setTextViewText(R.id.text_view_podcast_author_name, podcast.getAuthor());
            views.setTextViewText(R.id.text_view_podcast_name, podcast.getTitle());
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
