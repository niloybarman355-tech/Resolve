package com.resolve.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView web;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        web = new WebView(this);
        setContentView(web);

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);   // localStorage persists
        web.getSettings().setDatabaseEnabled(true);
        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setMediaPlaybackRequiresUserGesture(true);
        web.setBackgroundColor(0xFF11141A);

        // Keep all navigation inside this app's bundled page; open real links externally.
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest req) {
                String u = req.getUrl().toString();
                if (u.startsWith("file://")) return false;
                try {
                    startActivity(new android.content.Intent(
                        android.content.Intent.ACTION_VIEW, req.getUrl()));
                } catch (Exception ignored) {}
                return true;
            }
        });

        if (savedInstanceState == null) {
            web.loadUrl("file:///android_asset/index.html");
        } else {
            web.restoreState(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
}
