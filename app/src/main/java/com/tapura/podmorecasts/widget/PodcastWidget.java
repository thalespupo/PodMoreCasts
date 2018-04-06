package com.tapura.podmorecasts.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.tapura.podmorecasts.MyLog;
import com.tapura.podmorecasts.R;
import com.tapura.podmorecasts.details.PodcastDetailsActivity;

public class PodcastWidget extends AppWidgetProvider {
    public static final String ACTION_OPEN_PODCAST = "com.tapura.podmorecasts.widget.OPEN_PODCAST";
    public static final String EXTRA_ITEM = "com.tapura.podmorecasts.widget.EXTRA_ITEM";



    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        MyLog.d(PodcastWidget.class, "updateAppWidget");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.podcast_widget);
        views.setTextViewText(R.id.appwidget_text, context.getString(R.string.app_name));

        Intent intent = new Intent(context, WidgetService.class);

        views.setRemoteAdapter(R.id.list_view_podcasts, intent);
        views.setEmptyView(R.id.list_view_podcasts, R.id.widget_empty_view);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(ACTION_OPEN_PODCAST)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
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

