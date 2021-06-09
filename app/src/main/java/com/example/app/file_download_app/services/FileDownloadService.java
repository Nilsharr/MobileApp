package com.example.app.file_download_app.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.app.R;
import com.example.app.file_download_app.activities.FileDownloadActivity;
import com.example.app.file_download_app.models.FileInfo;
import com.example.app.utils.Utilities;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class FileDownloadService extends IntentService {

    private static final String ACTION_FILE_DOWNLOAD = "com.example.app.file_download_app.services.actionFileDownload";
    private static final String FILE_DOWNLOAD_URL_ADDRESS = "com.example.app.file_download_app.services.fileDownloadUrlAddress";
    public static final String FILE_DOWNLOAD_BROADCAST = "com.example.app.file_download_app.services.fileDownloadBroadcast";
    public static final String FILE_DOWNLOAD_INFO = "com.example.app.file_download_app.services.fileDownloadInfo";
    private static final String FILE_DOWNLOAD_CHANNEL_ID = "com.example.app.file_download_app.services.fileDownloadChannelID";

    private static final int FILE_DOWNLOAD_NOTIFICATION_ID = 1;
    private static final int BLOCK_SIZE = 8 * 1024;

    private NotificationManagerCompat notificationManagerCompat;

    public FileDownloadService() {
        super(FileDownloadService.class.getSimpleName());
    }

    public static void startService(Context context, String urlAddress) {
        Intent intent = new Intent(context, FileDownloadService.class).setAction(ACTION_FILE_DOWNLOAD);
        intent.putExtra(FILE_DOWNLOAD_URL_ADDRESS, urlAddress);
        context.startService(intent);
    }

    private void sendBroadcast(@Nullable FileInfo fileInfo) {
        Intent intent = new Intent(FILE_DOWNLOAD_BROADCAST);
        intent.putExtra(FILE_DOWNLOAD_INFO, fileInfo);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            if (intent.getAction().equals(ACTION_FILE_DOWNLOAD)) {
                // creating notification for file download
                createNotificationChannel();
                notificationManagerCompat.notify(FILE_DOWNLOAD_NOTIFICATION_ID, createDownloadInProgressNotification(0));

                // creating connection
                HttpsURLConnection connection = null;
                FileOutputStream fileOutputStream = null;
                FileInfo fileInfo = null;
                try {
                    URL url = new URL(intent.getStringExtra(FILE_DOWNLOAD_URL_ADDRESS));
                    File downloadedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + new File(url.getFile()).getName());

                    // deleting file in storage that has the same path and name as downloading file
                    if (downloadedFile.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        downloadedFile.delete();
                    }
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    fileInfo = new FileInfo(connection.getContentLength(), connection.getContentType().split("/")[1]);

                    DataInputStream dataInputStream = new DataInputStream(connection.getInputStream());
                    fileOutputStream = new FileOutputStream(downloadedFile.getPath());
                    byte[] buffer = new byte[BLOCK_SIZE];
                    int bytesRead, totalBytesRead = 0, currentPercent, previousPercent = -1;
                    while ((bytesRead = dataInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;

                        // updating notification and sending broadcast
                        // only when progress percent has changed
                        currentPercent = Utilities.percentOfNumber(totalBytesRead, fileInfo.getSize());
                        if (currentPercent != previousPercent) {
                            previousPercent = currentPercent;
                            fileInfo.setDataDownloaded(totalBytesRead);
                            sendBroadcast(fileInfo);
                            notificationManagerCompat.notify(FILE_DOWNLOAD_NOTIFICATION_ID, createDownloadInProgressNotification(currentPercent));
                        }
                    }

                    fileInfo.setResult(FileInfo.DOWNLOAD_SUCCESSFUL);
                    notificationManagerCompat.notify(FILE_DOWNLOAD_NOTIFICATION_ID, createDownloadSuccessfulNotification(downloadedFile));
                } catch (Exception e) {
                    // dismissing notification and sending broadcast with result of download as error
                    notificationManagerCompat.cancelAll();
                    if (fileInfo != null) {
                        fileInfo.setResult(FileInfo.DOWNLOAD_ERROR);
                        sendBroadcast(fileInfo);
                    }
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                Log.e("action", "unknown action");
            }
        }
    }

    // creating notification channel if android version is >= 8
    private void createNotificationChannel() {
        notificationManagerCompat = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(FILE_DOWNLOAD_CHANNEL_ID, getString(R.string.notification_channel_name_download), NotificationManager.IMPORTANCE_LOW);
            notificationManagerCompat.createNotificationChannel(notificationChannel);
        }
    }

    private Notification createDownloadInProgressNotification(int progress) {
        // sending user to file download activity on notification click
        Intent intent = new Intent(this, FileDownloadActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, FILE_DOWNLOAD_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(getString(R.string.notification_file_download_title))
                .setContentText(getString(R.string.notification_file_download_text_in_progress))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setProgress(100, progress, false);

        return notificationBuilder.build();
    }

    private Notification createDownloadSuccessfulNotification(File downloadedFile) {

        // getting type of downloaded file
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(downloadedFile.getName());
        String fileType = map.getMimeTypeFromExtension(fileExtension);
        if (fileType == null) {
            fileType = "/*/";
        }
        // opening downloaded file on notification click
        Intent intent = new Intent(Intent.ACTION_VIEW).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", downloadedFile), fileType);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, FILE_DOWNLOAD_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setOngoing(false)
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle(getString(R.string.notification_file_download_title))
                .setContentText(getString(R.string.notification_file_download_text_completed))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setProgress(100, 100, false);

        return notificationBuilder.build();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        notificationManagerCompat.cancelAll();
    }
}
