package com.example.app.file_download_app.services;

import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.example.app.file_download_app.models.FileInfo;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetFileInfoTask extends AsyncTask<String, Void, FileInfo> {

    final AsyncResponse delegate;

    // interface with method that returns info about file from given url address
    public interface AsyncResponse {
        void downloadedFileInfo(@Nullable FileInfo fileInfo);
    }

    @SuppressWarnings("deprecation")
    public GetFileInfoTask(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected FileInfo doInBackground(String... urlAddress) {
        HttpsURLConnection connection = null;
        FileInfo fileInfo = null;
        try {
            // creating connection
            URL url = new URL(urlAddress[0]);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Encoding", "identity");

            // saving info about file size and type
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
