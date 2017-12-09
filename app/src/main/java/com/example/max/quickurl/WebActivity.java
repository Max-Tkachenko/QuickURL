package com.example.max.quickurl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends Activity {

    private WebView mWebView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_url);

        String URL = getIntent().getStringExtra("URL");

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(URL);
    }

    public void backMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void refresh(View view){
        String URL = mWebView.getUrl();
        mWebView.loadUrl(URL);
    }
}
