package com.github.ricardobarbosa.wheresmymoney.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.github.ricardobarbosa.wheresmymoney.R;

/**
 * Implementation of App Widget functionality.
 */
public class CollectionWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.collection_widget);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, views, appWidgetIds);
        } else {
            setRemoteAdapterV11(context, views, appWidgetIds);
        }

        appWidgetManager.updateAppWidget(appWidgetIds, views);

//        appWidgetManager.updateAppWidget(new ComponentName(context, WidgetDataProvider.class), views);

    }

    @Override
    public void onEnabled(Context context) {}

    @Override
    public void onDisabled(Context context) {}

    /**
     * Sets the remote adapter used to fill in the list items
     *  @param views RemoteViews to set the RemoteAdapter
     * @param appWidgetIds
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views, int[] appWidgetIds) {
        Intent intent = new Intent(context, WidgetService.class);
//        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds );
        views.setRemoteAdapter(R.id.widget_list, intent);
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *  @param views RemoteViews to set the RemoteAdapter
     * @param appWidgetIds
     */
    @SuppressWarnings("deprec ation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views, int[] appWidgetIds) {
        Intent intent = new Intent(context, WidgetService.class);
//        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        views.setRemoteAdapter(0, R.id.widget_list, intent);
    }
}