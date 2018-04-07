package com.tapura.podmorecasts.download;


import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.tapura.podmorecasts.MyLog;
import com.tapura.podmorecasts.R;

class DownloadNotification {

    private static final String NOTIFICATION_CHANNEL_ID = "download_episode_channel_id_01";

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_NONE);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    public void createNotification(Context context, int downloadStatus, long refId) {
        createNotificationChannel(context);
        MyLog.e(getClass(), "Download finished, ID:" + refId);

        String notificationText;

        switch (downloadStatus) {
            case DownloadManager.STATUS_FAILED:
                notificationText = "Download failed, please try again";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                notificationText = "Download completed";
                break;
            default:
                notificationText = "Unknown status = " + downloadStatus;
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("PodMoreCasts")
                        .setContentText(notificationText);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, mBuilder.build());
        }
    }
}
