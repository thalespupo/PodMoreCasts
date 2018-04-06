package com.tapura.podmorecasts.widget;


import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import java.util.List;

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        MyLog.d(getClass(), "onGetViewFactory");

        return new PodcastRemoteViewsFactory();
    }

    private class PodcastRemoteViewsFactory implements RemoteViewsFactory {
        private Context mContext;
        private List<Podcast> mPodcastList;
        private FirebaseDb mDb;
        private ValueEventListener firebaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPodcastList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    Podcast podcast;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        podcast = data.getValue(Podcast.class);
                        podcast.setEpisodes(new ArrayList<>());
                        mPodcastList.add(podcast);
                    }
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, PodcastWidget.class));
                    AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view_podcasts);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        public PodcastRemoteViewsFactory() {
            MyLog.d(getClass(), "PodcastRemoteViewsFactory");
            mContext = getApplicationContext();

            mDb = new FirebaseDb();
            mDb.attachPodcastListListener(mContext, firebaseListener);
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
            if (mDb != null) {
                mDb.detachPodcastListListener(mContext, firebaseListener);
            }
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

            Bundle extras = new Bundle();
            extras.putString(PodcastWidget.EXTRA_ITEM, podcast.getFeedUrl());
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

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
