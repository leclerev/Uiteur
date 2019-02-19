package com.structit.apiclient.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import android.webkit.CookieManager;

import com.structit.apiclient.data.PlayItem;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetFileTask extends AsyncTask<String, Void, Boolean> {
    private static final String LOG_TAG = GetFileTask.class.getSimpleName();

    private ApiService mService;
    private PlayItem mItem;

    GetFileTask(ApiService service, PlayItem item) {
        mService = service;
        mItem = item;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        HttpURLConnection connection = null;

        Log.d(LOG_TAG, "URL: " + params[0]);

        try {
            int nbAttempt = 0;
            do {
                URL serviceURL = new URL(params[0]);
                String filename = params[1] + "_" + mItem.getId();

                Log.d(LOG_TAG, "Filename: " + filename);

                connection = (HttpURLConnection) serviceURL.openConnection();

                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(5000);

                CookieManager cookieManager = CookieManager.getInstance();
                String cookie = cookieManager.getCookie("http://uiteur.struct-it.fr/");
                if (cookie != null) {
                    connection.setRequestProperty("Cookie", cookie);
                } // Else do nothing

                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.i(LOG_TAG, "Connection granted...");

                    DataInputStream reader = new DataInputStream(connection.getInputStream());
                    FileOutputStream writer = this.mService.openFileOutput(filename, Context.MODE_PRIVATE);
                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = reader.read(buffer)) > 0) {
                        writer.write(buffer, 0, count);
                    }
                    reader.close();
                    writer.flush();
                    writer.close();

                    mItem.setFile(filename);
                } else {
                    if (connection.getResponseMessage() != null) {
                        Log.e(LOG_TAG, "Connection error: " + connection.getResponseMessage());
                    } else {
                        Log.e(LOG_TAG, "Connection refused...");
                    }
                }

                nbAttempt++;
            } while(mItem.getFile().length() == 0 && nbAttempt < 3);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            } // Else do nothing
        }

        return mItem.getFile().length() > 0;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        this.mService.notifyFile(success, mItem);
    }
}
