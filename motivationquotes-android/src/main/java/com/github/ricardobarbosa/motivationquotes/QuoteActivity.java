package com.github.ricardobarbosa.motivationquotes;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class QuoteActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView quoteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);

        imageView = (ImageView) findViewById(R.id.bg);
        quoteTextView = (TextView) findViewById(R.id.quote);

        FetchUnsplashImageTask task = new FetchUnsplashImageTask(new AsyncTaskDelegate() {
            @Override
            public void processFinish(Object o) {

                if (o != null){
                    Context context = imageView.getContext();
                    BlurTransformation transformation = new BlurTransformation(context);
                    Picasso.with(context).load(o.toString()).transform(transformation).into(imageView);
                }
            }
        });
        task.execute();

        EndpointsAsyncTask endpointsAsyncTask = new EndpointsAsyncTask(new AsyncTaskDelegate() {
            @Override
            public void processFinish(Object o) {
                if (o != null){
                    quoteTextView.setText(o.toString());
                }
            }
        });
        endpointsAsyncTask.execute();

    }

}
