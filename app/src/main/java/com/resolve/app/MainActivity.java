package com.resolve.app;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private WebView web;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createChannel();
        Native.rescheduleFromPrefs(this);

        web = new WebView(this);
        setContentView(web);

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setDatabaseEnabled(true);
        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setMediaPlaybackRequiresUserGesture(true);
        web.setBackgroundColor(0xFF11141A);
        web.addJavascriptInterface(new Bridge(), "Android");

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest req) {
                String u = req.getUrl().toString();
                if (u.startsWith("file://")) return false;
                try { startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, req.getUrl())); } catch (Exception ignored) {}
                return true;
            }
        });

        if (savedInstanceState == null) web.loadUrl("file:///android_asset/index.html");
        else web.restoreState(savedInstanceState);
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(
                ReminderReceiver.CHANNEL, "Daily reminders", NotificationManager.IMPORTANCE_DEFAULT);
            ch.setDescription("Your daily check-in nudge");
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(ch);
        }
    }

    private void ensureNotifPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.POST_NOTIFICATIONS"}, 9009);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) { super.onSaveInstanceState(outState); web.saveState(outState); }

    @Override
    public void onBackPressed() { if (web.canGoBack()) web.goBack(); else super.onBackPressed(); }

    /** Bridge exposed to the web app as window.Android. */
    private class Bridge {
        SharedPreferences prefs() { return getSharedPreferences(Native.PREFS, Context.MODE_PRIVATE); }

        @JavascriptInterface
        public void saveState(double startMillis, String rank) {
            prefs().edit().putLong("start", (long) startMillis).putString("rank", rank == null ? "Spark" : rank).apply();
            StreakWidget.updateAll(MainActivity.this);
        }

        @JavascriptInterface
        public void setReminder(int hour, int minute) {
            prefs().edit().putBoolean("remOn", true).putInt("remH", hour).putInt("remM", minute).apply();
            runOnUiThread(MainActivity.this::ensureNotifPermission);
            Native.scheduleReminder(MainActivity.this, hour, minute);
        }

        @JavascriptInterface
        public void cancelReminder() {
            prefs().edit().putBoolean("remOn", false).apply();
            Native.cancelReminder(MainActivity.this);
        }

        @JavascriptInterface
        public boolean notificationsAllowed() {
            if (Build.VERSION.SDK_INT < 33) return true;
            return ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED;
        }

        @JavascriptInterface
        public boolean available() { return true; }
    }
}
