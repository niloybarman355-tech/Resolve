package com.resolve.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
    static final String CHANNEL = "resolve_reminders";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        SharedPreferences p = ctx.getSharedPreferences(Native.PREFS, Context.MODE_PRIVATE);

        long start = p.getLong("start", System.currentTimeMillis());
        long days = Math.max(0, (System.currentTimeMillis() - start) / 86400000L);
        String body = days == 0
            ? "A fresh start today. Open Resolve and set your intention."
            : "You're on day " + days + ". Check in and keep the streak alive.";

        Intent open = new Intent(ctx, MainActivity.class);
        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= 23) flags |= PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pi = PendingIntent.getActivity(ctx, 0, open, flags);

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, CHANNEL)
            .setSmallIcon(R.drawable.ic_notify)
            .setContentTitle("Resolve")
            .setContentText(body)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pi);

        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        try { if (nm != null) nm.notify(101, b.build()); } catch (SecurityException ignored) {}

        // Re-arm for the next day.
        Native.scheduleReminder(ctx, p.getInt("remH", 20), p.getInt("remM", 0));
    }
}
