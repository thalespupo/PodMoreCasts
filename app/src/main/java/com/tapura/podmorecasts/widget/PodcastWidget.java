package com.tapura.podmorecasts.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.tapura.podmorecasts.MyLog;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.database.FirebaseDb;
import com.tapura.podmorecasts.model.Podcast;

import java.util.ArrayList;
import java.util.List;

public class PodcastWidget extends AppWidgetProvider {
    public static final String WIDGET_PODCASTS = "widget_podcasts";
    private Context mContext;
    private List<Podcast> mPodcastList;
    private FirebaseDb mDb;
    private ValueEventListener firebaseListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mPodcastList = new ArrayList<>();
            if (dataSnapshot.exists()) {
                Podcast podcast = new Podcast();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    podcast = data.getValue(Podcast.class);
                    podcast.setEpisodes(new ArrayList<>());
                    mPodcastList.add(podcast);
                }
            }
            for (int appWidgetId : mIds) {
                updateAppWidget(mContext, mManager, appWidgetId, mPodcastList);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    private AppWidgetManager mManager;
    private int[] mIds;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, List<Podcast> list) {

        MyLog.d(PodcastWidget.class, "updateAppWidget");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.podcast_widget);
        views.setTextViewText(R.id.appwidget_text, context.getString(R.string.app_name));

        Intent intent = new Intent(context, WidgetService.class);
        Gson gson = new Gson();
        String podcastList = gson.toJson(list);
        intent.putExtra(WIDGET_PODCASTS, podcastList);

        views.setRemoteAdapter(R.id.list_view_podcasts, intent);
        views.setEmptyView(R.id.list_view_podcasts, R.id.widget_empty_view);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mContext = context;
        mManager = appWidgetManager;
        mIds = appWidgetIds;
        mDb = new FirebaseDb();
        mDb.attachPodcastListListener(mContext, firebaseListener);
        MyLog.d(getClass(), "onUpdate");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        MyLog.d(getClass(), "onDeleted");
        if (mDb != null) {
            mDb.detachPodcastListListener(mContext, firebaseListener);
        }

    }

    @Override
    public void onEnabled(Context context) {
        MyLog.d(getClass(), "onEnabled");
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        MyLog.d(getClass(), "onDisabled");
        // Enter relevant functionality for when the last widget is disabled
    }
}

