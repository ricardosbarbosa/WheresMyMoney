package com.github.ricardobarbosa.wheresmymoney.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.ricardobarbosa.wheresmymoney.R;
import com.github.ricardobarbosa.wheresmymoney.model.Categoria;

public class CategoriaViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView mNomeView;
    public Categoria categoria;

    public CategoriaViewHolder(View view) {
        super(view);
        mView = view;
        mNomeView = (TextView) view.findViewById(R.id.valor);
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mNomeView.getText() + "'";
    }
}