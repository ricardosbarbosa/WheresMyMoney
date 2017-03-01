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
import com.github.ricardobarbosa.wheresmymoney.activity.CategoriaDetailActivity;
import com.github.ricardobarbosa.wheresmymoney.activity.CategoriaListActivity;
import com.github.ricardobarbosa.wheresmymoney.model.Categoria;

/**
 * A fragment representing a single Categoria detail screen.
 * This fragment is either contained in a {@link CategoriaListActivity}
 * in two-pane mode (on tablets) or a {@link CategoriaDetailActivity}
 * on handsets.
 */
public class CategoriaDetailFragment extends Fragment {

    /**
     * The dummy content this fragment is presenting.
     */
    private Categoria categoria;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CategoriaDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(Categoria.PARCELABLE_KEY)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            categoria = getArguments().getParcelable(Categoria.PARCELABLE_KEY);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(categoria.getNome());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.categoria_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (categoria != null) {
            ((TextView) rootView.findViewById(R.id.categoria_detail)).setText(categoria.getNome());
        }

        return rootView;
    }
}
