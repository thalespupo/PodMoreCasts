package com.tapura.podmorecasts.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.tapura.podmorecasts.MyLog;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.SplashActivity;
import com.tapura.podmorecasts.details.PodcastDetailsActivity;
import com.tapura.podmorecasts.main.MainActivity;

public class PodcastWidget extends AppWidgetProvider {
    public static final String ACTION_OPEN_PODCAST = "com.tapura.podmorecasts.widget.OPEN_PODCAST";
    public static final String EXTRA_ITEM = "com.tapura.podmorecasts.widget.EXTRA_ITEM";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        MyLog.d(PodcastWidget.class, "updateAppWidget");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.podcast_widget);
        views.setTextViewText(R.id.appwidget_text, context.getString(R.string.app_name));

        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntentToOpenApp = PendingIntent.getActivity(context, 0, appIntent, 0);

        Intent itemIntent = new Intent(context, PodcastWidget.class);
        itemIntent.setAction(ACTION_OPEN_PODCAST);
        itemIntent.setData(Uri.parse(itemIntent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntentToOpenPodcast = PendingIntent.getBroadcast(context, 0, itemIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent serviceIntent = new Intent(context, WidgetService.class);

        views.setOnClickPendingIntent(R.id.text_view_title, pendingIntentToOpenApp);
        views.setPendingIntentTemplate(R.id.list_view_podcasts, pendingIntentToOpenPodcast);
        views.setRemoteAdapter(R.id.list_view_podcasts, serviceIntent);
        views.setEmptyView(R.id.list_view_podcasts, R.id.widget_empty_view);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_OPEN_PODCAST)) {
            String feed = intent.getStringExtra(EXTRA_ITEM);
            context.startActivity(PodcastDetailsActivity.createIntent(context, feed, null));
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        MyLog.d(getClass(), "onUpdate");
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        MyLog.d(getClass(), "onDeleted");
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

