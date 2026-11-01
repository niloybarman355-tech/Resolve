package com.resolve.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

/** Shared helpers for scheduling the daily reminder. */
public final class Native {
    public static final String PREFS = "resolve_native";
    static final int REMINDER_REQ = 7001;

    private Native() {}

    static PendingIntent reminderPI(Context ctx) {
        Intent i = new Intent(ctx, ReminderReceiver.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= 23) flags |= PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getBroadcast(ctx, REMINDER_REQ, i, flags);
    }

    /** Schedule (or reschedule) the next daily reminder at hour:minute local time. */
    public static void scheduleReminder(Context ctx, int hour, int minute) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        Calendar next = Calendar.getInstance();
        next.set(Calendar.HOUR_OF_DAY, hour);
        next.set(Calendar.MINUTE, minute);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        if (next.getTimeInMillis() <= System.currentTimeMillis()) {
            next.add(Calendar.DAY_OF_YEAR, 1);
        }
        PendingIntent pi = reminderPI(ctx);
        try {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.getTimeInMillis(), pi);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, next.getTimeInMillis(), pi);
            }
        } catch (SecurityException e) {
            am.set(AlarmManager.RTC_WAKEUP, next.getTimeInMillis(), pi);
        }
    }

    public static void cancelReminder(Context ctx) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am != null) am.cancel(reminderPI(ctx));
    }

    /** Re-arm the reminder from saved prefs (used on boot and app start). */
    public static void rescheduleFromPrefs(Context ctx) {
        android.content.SharedPreferences p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (p.getBoolean("remOn", false)) {
            scheduleReminder(ctx, p.getInt("remH", 20), p.getInt("remM", 0));
        }
    }
}
