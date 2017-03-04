
package com.github.ricardobarbosa.motivationquotes;

import android.os.AsyncTask;

import com.github.ricardosbarbosa.ricardoslib.AsyncTaskDelegate;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchUnsplashImageTask extends AsyncTask<Void, Void, String> {

    private final AsyncTaskDelegate callback;

    public FetchUnsplashImageTask(AsyncTaskDelegate callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... params) {

        final String FORECAST_BASE_URL = "https://source.unsplash.com/category/nature/800x450";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(FORECAST_BASE_URL)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            return response.request().url().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "nada";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        callback.processFinish(s);
    }
}