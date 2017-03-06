package com.github.ricardobarbosa.wheresmymoney.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.ricardobarbosa.wheresmymoney.R;
import com.github.ricardobarbosa.wheresmymoney.model.Movimentacao;

public class MovimentacaoViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final View mBolinhaDespesa;
    public final View mBolinhaReceita;
    public final View mBolinhaTransferencia;
    public final TextView mDescricaoView;
    public final TextView mValorView;
    public final TextView mDataView;
    public final TextView mCategoriaView;
    public Movimentacao movimentacao;

    public MovimentacaoViewHolder(View view) {
        super(view);
        mView = view;
        mDescricaoView = (TextView) view.findViewById(R.id.descricao);
        mDataView = (TextView) view.findViewById(R.id.data);
        mValorView = (TextView) view.findViewById(R.id.valor);
        mBolinhaDespesa = (View) view.findViewById(R.id.bolinha_vermelha);
        mBolinhaReceita = (View) view.findViewById(R.id.bolinha_verde);
        mBolinhaTransferencia = (View) view.findViewById(R.id.bolinha_azul);
        mCategoriaView = (TextView) view.findViewById(R.id.categoria);
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mValorView.getText() + "'";
    }
}