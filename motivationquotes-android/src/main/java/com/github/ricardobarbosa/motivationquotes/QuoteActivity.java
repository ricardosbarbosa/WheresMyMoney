package com.github.ricardobarbosa.motivationquotes;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ricardosbarbosa.ricardoslib.AsyncTaskDelegate;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class QuoteActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView quoteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
