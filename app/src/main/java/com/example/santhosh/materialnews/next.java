package com.example.santhosh.materialnews;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.santhosh.materialnews.model.moviemodel;
import com.google.gson.Gson;

public class next extends Activity {


    private String abc;
    private WebView web;
    private ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        web=(WebView)findViewById(R.id.web);
        pb=(ProgressBar)findViewById(R.id.pb);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String json = bundle.getString("movieModel");
            moviemodel mod = new Gson().fromJson(json, moviemodel.class);
            abc=mod.getUrl();
            web.setWebViewClient(new mclient());
            WebSettings webSettings = web.getSettings();
            webSettings.setJavaScriptEnabled(true);
            web.loadUrl(abc);

        }
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
public class mclient extends WebViewClient {
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        pb.setVisibility(view.GONE);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return super.shouldOverrideUrlLoading(view, url);
    }
}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode==KeyEvent.KEYCODE_BACK)&&(web.canGoBack()))
        {
web.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
