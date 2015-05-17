package com.automatic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.automatic.android.sdk.R;
import com.automatic.android.sdk.exceptions.AutomaticSdkException;
import com.automatic.net.ResponsesPublic;

import java.util.UUID;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by duncancarroll on 2/12/15.
 * Copyright (c) 2015 Automatic Labs. All rights reserved.
 */
public class LoginActivity extends Activity {

    private final String TAG = LoginActivity.this.getClass().getSimpleName();

    private String AUTH_URL_BASE = "https://www.automatic.com/oauth/";
    private WebView mWebView;
    private ProgressDialog mLoadingSpinner;
    private String mCode;
    private String mCacheBuster;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mLoadingSpinner = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
        mWebView = (WebView) findViewById(R.id.auth_webview);

        initWebView();

        // load the url!
        mWebView.loadUrl(AUTH_URL_BASE + "authorize/" + "?response_type=code&client_id=" + Internal.get().getClientId() + "&scope=" + Automatic.getScopes() + mCacheBuster);
    }

    private void initWebView() {
        // this is the only thing that will remove the auth creds in webview
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        // Paranoidly also use old-school cachebuster to avoid caching
        mCacheBuster = "#"+ UUID.randomUUID().toString();

        showLoadingSpinner();

        mWebView.clearCache(true);
        mWebView.clearHistory();
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // don't save form data b/c that's bad.
        webSettings.setSaveFormData(false);
        mWebView.setWebViewClient(new SdkWebViewClient());

    }

    @Override
    public void onResume() {
        super.onResume();
        // always clear the cache
        mWebView.clearCache(true);
        mWebView.clearHistory();
    }

    // might not need this
    @Override
    public void onStop() {
        super.onStop();
        mWebView.clearCache(true);
        mWebView.clearHistory();
    }

    /**
     * Intercepts webview URL loads and allows us to grab the code
     */
    private class SdkWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            hideLoadingSpinner();
            view.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "Inside shouldOverrideUrlLoading() with url: " + url);
            // is our code present in the URL?  If so, pull it out.
            final String MARKER = "code=";
            if (url.toLowerCase().contains(MARKER.toLowerCase())) {
                int index = url.indexOf(MARKER)+MARKER.length(); // indexOf(MARKER) will get me the beginning of the marker, but we want the end, so add the marker length.
                // token is always a 40-character string preceded by "&code="
                try {
                    mCode = url.substring(index, index + 40);
                    showLoadingSpinner();
                    Log.i(TAG, "Got token: " + mCode);
                }catch(StringIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    throw new AutomaticSdkException("Error: No authorize code was returned or could be found within the response.  Check your client id and try again.");
                }
                // got the initial token, now turn around and get our auth_token
                getToken(mCode);
                return true;
            }
            return false;
        }
    }

    private void getToken(String mCode) {

        Automatic.sClient.getTokenApi(mCode, new Callback<ResponsesPublic.OAuthResponse>() {
            @Override
            public void success(ResponsesPublic.OAuthResponse oAuthResponse, Response response) {

                Internal.get().setToken(oAuthResponse);
                hideLoadingSpinner();
                Automatic.get().getButton().invokeCallbacks(true);
                // set the button state as soon as we can
                Automatic.get().getButton().updateStyles();
                // technically we're done, so finish us out and call our success handler
                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                // TODO if the user has defined a failure callback, we call that, otherwise we fail out with a Toast
                hideLoadingSpinner();
                Log.e(TAG, "Error: " + error.getResponse().getStatus() + ", " + error.getMessage());

                Boolean userHandledCallback = Automatic.get().getButton().invokeCallbacks(false, error);

                // show a toast error to the user by default
                if (!userHandledCallback) {
                    Toast.makeText(LoginActivity.this, "Error communicating with Automatic\'s servers.  Please try again later.", Toast.LENGTH_LONG).show();
                }

                Internal.get().reset();
                // did I mention we always clear the cache?
                mWebView.clearCache(true);
                mWebView.clearHistory();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    private void showLoadingSpinner() {
        // show loading dialog
        if (mLoadingSpinner != null) {
            mLoadingSpinner.setMessage("Loading...");
        }
        mLoadingSpinner.show();
    }

    private void hideLoadingSpinner() {
        if (mLoadingSpinner != null) {
            mLoadingSpinner.dismiss();
        }
    }
}
