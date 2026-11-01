package com.resolve.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class StreakWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) render(ctx, mgr, id);
    }

    static void render(Context ctx, AppWidgetManager mgr, int id) {
        SharedPreferences p = ctx.getSharedPreferences(Native.PREFS, Context.MODE_PRIVATE);
        long start = p.getLong("start", System.currentTimeMillis());
        long days = Math.max(0, (System.currentTimeMillis() - start) / 86400000L);
        String rank = p.getString("rank", "Spark");

        RemoteViews rv = new RemoteViews(ctx.getPackageName(), R.layout.widget_streak);
        rv.setTextViewText(R.id.wDays, String.valueOf(days));
        rv.setTextViewText(R.id.wLabel, days == 1 ? "DAY CLEAN" : "DAYS CLEAN");
        rv.setTextViewText(R.id.wRank, rank);

        Intent open = new Intent(ctx, MainActivity.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= 23) flags |= PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pi = PendingIntent.getActivity(ctx, 0, open, flags);
        rv.setOnClickPendingIntent(R.id.wRoot, pi);

        mgr.updateAppWidget(id, rv);
    }

    /** Refresh every placed widget (called from the app when state changes). */
    public static void updateAll(Context ctx) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(ctx);
        ComponentName cn = new ComponentName(ctx, StreakWidget.class);
        int[] ids = mgr.getAppWidgetIds(cn);
        for (int id : ids) render(ctx, mgr, id);
    }
}
