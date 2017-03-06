package com.github.ricardobarbosa.wheresmymoney.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.ricardobarbosa.wheresmymoney.R;
import com.github.ricardobarbosa.wheresmymoney.activity.MovimentacaoDetailActivity;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract;
import com.github.ricardobarbosa.wheresmymoney.fragment.MovimentacaoDetailFragment;
import com.github.ricardobarbosa.wheresmymoney.holder.MovimentacaoViewHolder;
import com.github.ricardobarbosa.wheresmymoney.model.EnumMovimentacaoTipo;
import com.github.ricardobarbosa.wheresmymoney.model.Movimentacao;
import com.github.ricardobarbosa.wheresmymoney.util.CursorRecyclerViewAdapter;

import java.text.SimpleDateFormat;

/**
 * Created by skyfishjy on 10/31/14.
 */
public class MovimentacaoCursorAdapter extends CursorRecyclerViewAdapter<MovimentacaoViewHolder> implements ItemTouchHelperAdapter {

    private static final String TAG = "MovimentacaoCursorAdapter";
    boolean mTwoPane;

    private final OnStartDragListener mDragStartListener;

    public MovimentacaoCursorAdapter(Context context, Cursor cursor, boolean mTwoPane, OnStartDragListener dragStartListener){
        super(context,cursor);
        this.mTwoPane = mTwoPane;
        mDragStartListener = dragStartListener;
    }

    @Override
    public MovimentacaoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movimentacao_list_content, parent, false);
        MovimentacaoViewHolder vh = new MovimentacaoViewHolder(itemView);
        return vh;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(MovimentacaoViewHolder viewHolder, Cursor cursor) {
        final MovimentacaoViewHolder holder = viewHolder;

        Log.v(TAG, String.valueOf(cursor.getPosition()));
        Movimentacao movimentacao = new Movimentacao(cursor);
        holder.movimentacao = movimentacao;
        holder.mDescricaoView.setText(movimentacao.getDescricao());
        holder.mValorView.setText(movimentacao.getValor().toString());
        holder.mCategoriaView.setText(movimentacao.getCategoria().getNome());
        holder.mBolinhaDespesa.setVisibility(movimentacao.getTipo().equals(EnumMovimentacaoTipo.DESPESA) ? View.VISIBLE : View.INVISIBLE) ;
        holder.mBolinhaReceita.setVisibility(movimentacao.getTipo().equals(EnumMovimentacaoTipo.RECEITA) ? View.VISIBLE : View.INVISIBLE) ;
        holder.mBolinhaTransferencia.setVisibility(movimentacao.getTipo().equals(EnumMovimentacaoTipo.TRANSFERENCIA) ? View.VISIBLE : View.INVISIBLE) ;
        String dataStr = (new SimpleDateFormat("dd/MM/yyyy")).format(movimentacao.getData());
        holder.mDataView.setText(dataStr);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(Movimentacao.PARCELABLE_KEY, holder.movimentacao);
                    MovimentacaoDetailFragment fragment = new MovimentacaoDetailFragment();
                    fragment.setArguments(arguments);
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movimentacao_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MovimentacaoDetailActivity.class);
                    intent.putExtra(Movimentacao.PARCELABLE_KEY, holder.movimentacao);

                    context.startActivity(intent);
                }
            }
        });

        // Start a drag whenever the handle view it touched
        holder.mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {

        String selection = WIMMContract.MovimentacaoEntry._ID + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(getItemId(position))};
        int rows = mContext.getContentResolver().delete(WIMMContract.MovimentacaoEntry.CONTENT_URI, selection, selectionArgs);

        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
//        notifyItemMoved(fromPosition, toPosition);
        return false;
    }
}