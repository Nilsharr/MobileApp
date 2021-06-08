package com.example.app.file_download_app;

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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class FileDownloadService extends IntentService {

    private static final String ACTION_FILE_DOWNLOAD = "com.example.app.file_download_app.actionDownload";
    private static final String FILE_DOWNLOAD_URL_ADDRESS = "com.example.app.file_download_app.urlAddress";
    private static final int FILE_DOWNLOAD_NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "channel";
    private static final int BLOCK_SIZE = 8 * 1024;
    public static String BROADCAST = "com.example.app.file_download_app.receiver";
    public static String SAVED_FILE = "savedfile";
    private NotificationManagerCompat notificationManager;

    public static void startService(Context context, String urlAddress) {
        Intent intent = new Intent(context, FileDownloadService.class).setAction(ACTION_FILE_DOWNLOAD);
        intent.putExtra(FILE_DOWNLOAD_URL_ADDRESS, urlAddress);
        context.startService(intent);
    }

    public FileDownloadService() {
        super("FileDownloadService");
    }

    private void sendBroadcast(@Nullable FileInfo fileInfo) {
        Intent intent = new Intent(BROADCAST);
        intent.putExtra(SAVED_FILE, fileInfo);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            if (intent.getAction().equals(ACTION_FILE_DOWNLOAD)) {
                createNotificationChannel();
                notificationManager.notify(FILE_DOWNLOAD_NOTIFICATION_ID, createNotification(0));

                HttpsURLConnection connection = null;
                FileOutputStream fileOutputStream = null;
                FileInfo fileInfo = null;
                try {
                    URL url = new URL(intent.getStringExtra(FILE_DOWNLOAD_URL_ADDRESS));
                    File downloadedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + new File(url.getFile()).getName());

                    if (downloadedFile.exists()) {
                        boolean deleteResult = downloadedFile.delete();
                    }
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    fileInfo = new FileInfo(connection.getContentLength(), connection.getContentType().split("/")[1]);

                    DataInputStream reader = new DataInputStream(connection.getInputStream());
                    fileOutputStream = new FileOutputStream(downloadedFile.getPath());
                    byte[] buffer = new byte[BLOCK_SIZE];
                    int bytesRead, totalBytesDownloaded = 0, currentPercent, previousPercent = -1;
                    while ((bytesRead = reader.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                        totalBytesDownloaded += bytesRead;

                        currentPercent = (int) (100 * (totalBytesDownloaded / (double) connection.getContentLength()));
                        if (previousPercent != currentPercent) {
                            Log.d("d", String.valueOf((int) (100 * (totalBytesDownloaded / (double) connection.getContentLength()))));
                            previousPercent = currentPercent;
                            fileInfo.setDataDownloaded(totalBytesDownloaded);
                            sendBroadcast(fileInfo);
                            notificationManager.notify(FILE_DOWNLOAD_NOTIFICATION_ID, createNotification(currentPercent));
                        }
                        //Log.d("hmm", bytesRead + "  " + totalBytesDownloaded + "  " + connection.getContentLength());
                    }

                    fileInfo.setResult(FileInfo.DOWNLOAD_SUCCESSFUL);
                    notificationManager.notify(FILE_DOWNLOAD_NOTIFICATION_ID, createFinishNotification(downloadedFile));
                    Log.d("hmm", "download complete");
                } catch (Exception e) {
                    if (fileInfo != null) {
                        fileInfo.setResult(FileInfo.DOWNLOAD_ERROR);
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
                Log.e("int", "unknown action");
            }
        }
    }

    private void createNotificationChannel() {
        notificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Name", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification(int progress) {
        Intent intent = new Intent(this, FileDownloadActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(getString(R.string.notification_file_download_title))
                .setContentText(getString(R.string.notification_file_download_text_in_progress))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setProgress(100, progress, false);

        return notificationBuilder.build();
    }


    private Notification createFinishNotification(File downloadedFile) {

        MimeTypeMap map = MimeTypeMap.getSingleton();
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(downloadedFile.getName());
        String fileType = map.getMimeTypeFromExtension(fileExtension);
        if (fileType == null) {
            fileType = "/*/";
        }
        Intent intentOpenFile = new Intent(Intent.ACTION_VIEW).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentOpenFile.setDataAndType(FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", downloadedFile), fileType);
        PendingIntent pendingIntentOpenFile = PendingIntent.getActivity(this, 0, intentOpenFile, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntentOpenFile)
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
        notificationManager.cancelAll();
    }
}
