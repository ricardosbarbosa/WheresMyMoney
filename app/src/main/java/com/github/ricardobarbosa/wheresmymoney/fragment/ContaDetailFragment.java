package com.github.ricardobarbosa.wheresmymoney.fragment;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ricardobarbosa.wheresmymoney.R;
import com.github.ricardobarbosa.wheresmymoney.activity.ContaDetailActivity;
import com.github.ricardobarbosa.wheresmymoney.activity.ContaListActivity;
import com.github.ricardobarbosa.wheresmymoney.model.Conta;

/**
 * A fragment representing a single Conta detail screen.
 * This fragment is either contained in a {@link ContaListActivity}
 * in two-pane mode (on tablets) or a {@link ContaDetailActivity}
 * on handsets.
 */
public class ContaDetailFragment extends Fragment {

    /**
     * The dummy content this fragment is presenting.
     */
    private Conta conta;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContaDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(Conta.PARCELABLE_KEY)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            conta = getArguments().getParcelable(Conta.PARCELABLE_KEY);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(conta.getNome());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.conta_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (conta != null) {
            ((TextView) rootView.findViewById(R.id.conta_detail)).setText(conta.getSaldo().toString());
        }

        return rootView;
    }
}
