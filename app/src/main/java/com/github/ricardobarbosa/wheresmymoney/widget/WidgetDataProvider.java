package com.github.ricardobarbosa.wheresmymoney.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.github.ricardobarbosa.wheresmymoney.R;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract;

/**
 * WidgetDataProvider acts as the adapter for the collection view widget,
 * providing RemoteViews to the widget in the getViewAt method.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "WidgetDataProvider";

    Cursor cursor;
    Context mContext = null;


    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        cursor.moveToPosition(position);

        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.list_item_quote);

        view.setTextViewText(R.id.id, cursor.getString(cursor.getColumnIndex(WIMMContract.ContaEntry.COLUMN_NOME)));
        view.setTextViewText(R.id.valor, cursor.getString(cursor.getColumnIndex(WIMMContract.ContaEntry.COLUMN_SALDO)));

        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        cursor = null;
        this.cursor = mContext.getContentResolver().query(
                WIMMContract.ContaEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );


    }

}