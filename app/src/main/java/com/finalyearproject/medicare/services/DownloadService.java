package com.finalyearproject.medicare.services;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DownloadService extends IntentService {

    private static final String DOWNLOAD_PATH = "com.finalyearproject.medicare_DownloadService_Download_path";
    private static final String DESTINATION_PATH = "com.finalyearproject.medicare_DownloadService_Destination_path";

    public DownloadService() {
        super("DownloadSongService");
    }

    public static Intent getDownloadService(final @NonNull Context callingClassContext, final @NonNull String downloadPath, final @NonNull String destinationPath) {
        return new Intent(callingClassContext, DownloadService.class)
                .putExtra(DOWNLOAD_PATH, downloadPath)
                .putExtra(DESTINATION_PATH, destinationPath);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String downloadPath = intent.getStringExtra(DOWNLOAD_PATH);
        String destinationPath = intent.getStringExtra(DESTINATION_PATH);
        try {
            startDownload(downloadPath, destinationPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startDownload(String downloadPath, String destinationPath) throws Exception {
        Uri uri = Uri.parse(downloadPath); // Path where you want to download file.
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("Download Report"); // Title for notification.
        request.setDescription("Downloading");
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(destinationPath, uri.getLastPathSegment());  // Storage directory path
        ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading
    }
}
