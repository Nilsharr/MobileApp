package com.example.app.file_download_app;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetFileInfoTask extends AsyncTask<String, Void, FileInfo> {

    public interface AsyncResponse {
        void downloadedFileInfo(@Nullable FileInfo fileInfo);
    }

    final AsyncResponse delegate;

    public GetFileInfoTask(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected FileInfo doInBackground(String... urlAddress) {
        HttpsURLConnection connection = null;
        FileInfo fileInfo = null;
        try {
            URL url = new URL(urlAddress[0]);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Encoding", "identity");

            fileInfo = new FileInfo(connection.getContentLength(), connection.getContentType().split("/")[1]);
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
        delegate.downloadedFileInfo(fileInfo);
    }
}
