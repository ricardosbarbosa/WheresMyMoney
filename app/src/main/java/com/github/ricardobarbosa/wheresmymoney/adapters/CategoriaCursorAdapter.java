package com.github.ricardobarbosa.wheresmymoney.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ricardobarbosa.wheresmymoney.R;
import com.github.ricardobarbosa.wheresmymoney.activity.CategoriaDetailActivity;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract;
import com.github.ricardobarbosa.wheresmymoney.fragment.CategoriaDetailFragment;
import com.github.ricardobarbosa.wheresmymoney.holder.CategoriaViewHolder;
import com.github.ricardobarbosa.wheresmymoney.model.Categoria;
import com.github.ricardobarbosa.wheresmymoney.util.CursorRecyclerViewAdapter;

/**
 * Created by skyfishjy on 10/31/14.
 */
public class CategoriaCursorAdapter extends CursorRecyclerViewAdapter<CategoriaViewHolder> {

    boolean mTwoPane;

    public CategoriaCursorAdapter(Context context, Cursor cursor, boolean mTwoPane){
        super(context,cursor);
        this.mTwoPane = mTwoPane;;
    }

    @Override
    public CategoriaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.categoria_list_content, parent, false);
        CategoriaViewHolder vh = new CategoriaViewHolder(itemView);
        return vh;
    }


    @Override
    public void onBindViewHolder(CategoriaViewHolder viewHolder, Cursor cursor) {
        final CategoriaViewHolder holder = viewHolder;

        Categoria categoria = new Categoria(cursor);
        holder.categoria = categoria;
        holder.mNomeView.setText(categoria.getNome());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(Categoria.PARCELABLE_KEY, holder.categoria);
                    CategoriaDetailFragment fragment = new CategoriaDetailFragment();
                    fragment.setArguments(arguments);
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.categoria_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, CategoriaDetailActivity.class);
                    intent.putExtra(Categoria.PARCELABLE_KEY, holder.categoria);

                    context.startActivity(intent);
                }
            }
        });
    }



}