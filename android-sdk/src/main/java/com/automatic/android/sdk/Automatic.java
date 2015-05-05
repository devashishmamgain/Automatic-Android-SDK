package com.automatic.android.sdk;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.automatic.net.AutomaticClientPublic;
import com.automatic.net.AutomaticRestApi;
import com.automatic.net.OAuthHandler;
import com.automatic.net.ResponsesPublic;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Entry point for SDK.  Call initialize() to begin a session.
 *
 * Created by duncancarroll on 2/12/15.
 * Copyright (c) 2015 Automatic Labs. All rights reserved.
 */
public class Automatic {

    public static final int REQUEST_CODE = 0x5afe;

    protected static Automatic sAutomatic;
    private Context context;
    protected static AutomaticClientPublic sClient;
    protected static LoginButton mLoginButton;
    protected static AutomaticLoginCallbacks mLoginCallbacks;
    protected static List<Scope> mScopes = new ArrayList<>();

    /**
     * Entry point to the SDK.  Pass in a Builder object and the client will be created.
     * Note that before API calls can be made, the user must be logged in.
     * @param builder Builder instance
     */
    public static void initialize(Builder builder) {
        builder.build();
        sClient = new AutomaticClientPublic(oAuthHandler, builder.logLevel);
    }

    /**
     * Builder class to create an SDK instance.
     */
    public static class Builder {

        private String clientSecret;
        public RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.NONE;

        public Builder(Context context) {
            sAutomatic = new Automatic();
            sAutomatic.context = context.getApplicationContext();
        }

        /**
         * Add multiple scopes here, e.g.:
         * addScopes(Scope.Public, Scope.Location, Scope.Vin);
         * @param scopes varargs of scopes to add.  Old scopes will not be deleted.  If a scope is already added, it will be skipped.
         * @return instance of Builder
         */
        public Builder addScopes(Scope[] scopes) {
            for (Scope scope: scopes) {
                addScope(scope);
            }
            return this;
        }

        /**
         * Add an individual scope to the API context
         * @param scope the scope to add
         * @return Builder instance
         */
        public Builder addScope(Scope scope) {
            // only add if we haven't added it already
            if (!sAutomatic.mScopes.contains(scope)) {
                sAutomatic.mScopes.add(scope);
            }
            return this;
        }

        /**
         * Add a LoginButton instance and set the default click handler
         * @param loginButton your LoginButton
         * @param activity your Activity
         * @return Builder instance
         */
        public Builder useLoginButton(LoginButton loginButton, final Activity activity) {
            sAutomatic.mLoginButton = loginButton;
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // not logged in
                    if (!Internal.get().hasToken()) {
                        loginWithAutomatic(activity);
                    } else {
                        // already logged-in
                        // show "really log out?"
                        sAutomatic.mLoginButton.showLogoutConfirmDialog(activity);
                    }

                }
            });
            return this;
        }

        /**
         * Set the log level of the SDK
         * @param level Retrofit log level
         * @return Builder instance
         */
        public Builder logLevel(RestAdapter.LogLevel level) {
            logLevel = level;
            return this;
        }

        /**
         * Called within the Builder to allow
         * usage of the AutomaticService binding
         * (when available).  Note:  Before
         * the service can be bound, the user
         * must be logged in with OAuth.
         *
         * @return Builder instance
         */
        public Builder useServiceBinding(Handler serviceMessageHandler) {
            // do a bunch of stuff to initialize our service binding, and keep
            // an instance of our Handler.
            return this;
        }

        private void build() {
            // initialize the Internal context
            Internal.init(sAutomatic.context);
            // set initial button state post-init
            if (mLoginButton != null) {
                mLoginButton.updateStyles();
            }
        }
    }

    protected static Automatic get() {
        return sAutomatic;
    }

    protected LoginButton getButton() {
        return mLoginButton;
    }

    /**
     * Method to manually invoke the OAuth login flow (for example if you are creating your own login button)
     * @param activity Activity context
     */
    public static void loginWithAutomatic(Activity activity) {
        // try to launch
        launchActivityHandler(activity);
    }

    private static void launchActivityHandler(Activity activity) {

        Intent launchIntent = new Intent(activity, LoginActivity.class);
        try {
            activity.startActivityForResult(launchIntent, REQUEST_CODE);
        }catch(ActivityNotFoundException e) {
            Log.e("AutomaticApi", "Error: You must add com.automatic.sdk.LoginActivity to your AndroidManifest");
            throw e;
        }
    }

    public static AutomaticRestApi restApi() {
        return AutomaticClientPublic.restApi();
    }

    public static boolean isLoggedIn() {
        return Internal.get().hasToken();
    }

    public static void logout() {
        // remove token
        Internal.get().reset();
        // also update button state (if it's not null)
        if (mLoginButton != null) {
            mLoginButton.updateStyles();
        }
    }

    protected static String getScopes() {
        String scopes = "";
        for (Scope scope : mScopes) {
            scopes += scope.serverName() + "%20";
        }
        return scopes;
    }


    public void setLoginCallbackListener(AutomaticLoginCallbacks callbacks) {
        this.mLoginCallbacks = callbacks;
    }

    public static void invokeCallback(Boolean success, RetrofitError error) {
        if (success) {
            mLoginCallbacks.onLoginSuccess();
        }else {
            mLoginCallbacks.onLoginFailure(error);
        }
    }

//
//    // set context (used by Builder)
//    private void setContext(Context context) {
//        this.context = context.getApplicationContext();
//
//        // gotta have permissions
//        if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
//            throw new AutomaticSdkException("Please add android.permission.INTERNET to your app manifest");
//        }
//    }


    /**
     * OAuth refresh handlers
     */
    private static OAuthHandler oAuthHandler = new OAuthHandler() {

        @Override
        public ResponsesPublic.OAuthResponse getToken() {
            return Internal.get().getToken();
        }

        @Override
        public void setToken(ResponsesPublic.OAuthResponse token) {
            Internal.get().setToken(token);
        }

        @Override
        public boolean refreshToken() {
            ResponsesPublic.OAuthResponse newToken = sClient.refreshTokenSync(Internal.get().getToken().refresh_token);
            if (newToken != null) {
                Internal.get().setToken(newToken);
                return true;
            }
            return false;
        }

        @Override
        public String getClientId() {
            return Internal.get().getClientId();
        }

        @Override
        public void onRefreshFailed() {
            // TODO handle oAuth token refresh failure
        }
    };

}