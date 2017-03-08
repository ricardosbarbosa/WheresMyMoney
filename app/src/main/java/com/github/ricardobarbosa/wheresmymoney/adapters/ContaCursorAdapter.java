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
import com.github.ricardobarbosa.wheresmymoney.activity.ContaDetailActivity;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract;
import com.github.ricardobarbosa.wheresmymoney.fragment.ContaDetailFragment;
import com.github.ricardobarbosa.wheresmymoney.holder.ContaViewHolder;
import com.github.ricardobarbosa.wheresmymoney.model.Conta;
import com.github.ricardobarbosa.wheresmymoney.util.CursorRecyclerViewAdapter;

/**
 * Created by skyfishjy on 10/31/14.
 */
public class ContaCursorAdapter extends CursorRecyclerViewAdapter<ContaViewHolder> {

    boolean mTwoPane;

    public ContaCursorAdapter(Context context, Cursor cursor, boolean mTwoPane){
        super(context,cursor);
        this.mTwoPane = mTwoPane;
    }

    @Override
    public ContaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conta_list_content, parent, false);
        ContaViewHolder vh = new ContaViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ContaViewHolder viewHolder, Cursor cursor) {
        final ContaViewHolder holder = viewHolder;

        Conta conta = new Conta(cursor);
        holder.conta = conta;
        holder.mIdView.setText(conta.getNome());
        holder.mContentView.setText(conta.getSaldo().toString());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(Conta.PARCELABLE_KEY, holder.conta);
                    ContaDetailFragment fragment = new ContaDetailFragment();
                    fragment.setArguments(arguments);
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.conta_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ContaDetailActivity.class);
                    intent.putExtra(Conta.PARCELABLE_KEY, holder.conta);

                    context.startActivity(intent);
                }
            }
        });
    }

}