package com.example.app.file_download_app;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class GetFileInfo extends AsyncTask<String, Void, FileInfo> {

    public interface AsyncResponse {
        void processFinish(FileInfo file);
    }

    final AsyncResponse delegate;

    public GetFileInfo(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected FileInfo doInBackground(String... strings) {
        HttpURLConnection connection = null;
        FileInfo fileInfo = null;
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Encoding", "identity");

            Log.d("d", String.format("%.2f MB", connection.getContentLength() / 1048576.0));
            Log.d("d", connection.getContentType());
            fileInfo = new FileInfo();
            fileInfo.setFileSize(String.format(Locale.getDefault(), "%.2f MB", connection.getContentLength() / 1048576.0));
            fileInfo.setFileType(connection.getContentType().split("/")[1]);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return fileInfo;
    }

    @Override
    protected void onPostExecute(FileInfo fileInfo) {
        delegate.processFinish(fileInfo);
    }
}
