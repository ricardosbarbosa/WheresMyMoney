package com.github.ricardobarbosa.motivationquotes;

import android.os.AsyncTask;

import com.example.ricardobarbosa.myapplication.backend.myApi.MyApi;
import com.github.ricardosbarbosa.ricardoslib.AsyncTaskDelegate;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class EndpointsAsyncTask extends AsyncTask<Void, Void, String> {
    private static MyApi myApiService = null;
    private final AsyncTaskDelegate delegate;

    public EndpointsAsyncTask( AsyncTaskDelegate delegate){
        this.delegate = delegate;
    }
    @Override
    protected String doInBackground(Void...params) {
        if(myApiService == null) {  // Only do this once
//            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
//                    new AndroidJsonFactory(), null)
//                // options for running against local devappserver
//                // - 10.0.2.2 is localhost's IP address in Android emulator
//                // - turn off compression when running against local devappserver
//                .setRootUrl("http://10.0.2.2:8080/_ah/api/")
//                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//                    @Override
//                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
//                        abstractGoogleClientRequest.setDisableGZipContent(true);
//                    }
//                });
//                // end options for devappserver

            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://randommotivationquotes.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            return myApiService.randomQuote().execute().getData();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (delegate != null) {
            delegate.processFinish(result);
        }
    }
}