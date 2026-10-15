package com.mossmoon.studytimer;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.graphics.Color;
import android.view.Window;

import androidx.webkit.WebViewAssetLoader;

public class MainActivity extends Activity {
    private WebView webView;
    private WebViewAssetLoader assetLoader;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setStatusBarColor(Color.rgb(23, 45, 39));
        getWindow().setNavigationBarColor(Color.rgb(23, 45, 39));
        getWindow().getDecorView().setSystemUiVisibility(0);

        assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(23, 45, 39));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMediaPlaybackRequiresUserGesture(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public android.webkit.WebResourceResponse shouldInterceptRequest(
                    WebView view, WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Keep navigation inside the app's own bundled assets only.
                return !("https".equals(request.getUrl().getScheme())
                        && "appassets.androidplatform.net".equals(request.getUrl().getHost()));
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        setContentView(webView);
        if (state == null) {
            webView.loadUrl("https://appassets.androidplatform.net/assets/index.html");
        } else {
            webView.restoreState(state);
            if (webView.getUrl() == null) {
                webView.loadUrl("https://appassets.androidplatform.net/assets/index.html");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        if (webView != null) webView.saveState(out);
        super.onSaveInstanceState(out);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
