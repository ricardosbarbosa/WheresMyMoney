package com.github.ricardobarbosa.wheresmymoney.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.github.ricardobarbosa.motivationquotes.QuoteActivity;
import com.github.ricardobarbosa.wheresmymoney.R;
import com.github.ricardobarbosa.wheresmymoney.fragment.CategoriaDetailFragment;
import com.github.ricardobarbosa.wheresmymoney.fragment.MovimentacaoFormFragment;
import com.github.ricardobarbosa.wheresmymoney.fragment.TransferenciaFormFragment;
import com.github.ricardobarbosa.wheresmymoney.model.EnumMovimentacaoTipo;

public class TransferenciaFormActivity extends AppCompatActivity {
    private static final String FORM_FRAGMENT_KEY = "nd_form";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FragmentManager fm = getSupportFragmentManager();
        TransferenciaFormFragment formFragment;

        Fragment retainedFragment = fm.findFragmentByTag(FORM_FRAGMENT_KEY);
        if (retainedFragment != null && retainedFragment instanceof TransferenciaFormFragment) {
            formFragment = (TransferenciaFormFragment) retainedFragment;
        } else {
            formFragment = new TransferenciaFormFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, formFragment, FORM_FRAGMENT_KEY)
                    .commit();
        }

        setSubmitAction(formFragment);
    }

    private void setSubmitAction(final TransferenciaFormFragment formFragment) {
        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formFragment.validate()) {
                    NavUtils.navigateUpFromSameTask((Activity) v.getContext());
                }
            }
        });
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