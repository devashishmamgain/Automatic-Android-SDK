package com.automatic.android.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import com.automatic.net.ResponsesPublic;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by duncancarroll on 4/2/15.
 * Copyright (c) 2015 Automatic Labs. All rights reserved.
 */
public class Internal {

    public static final String CLIENT_ID_METADATA = "com.automatic.sdk.client_id";
    private final String SHARED_PREFS_KEY = "com.automatic.sdk";
    private static final String TOKEN_KEY = "key_token";
    protected static Internal internal;
    private String clientId;
    private String clientSecret;
    static SharedPreferences sharedPrefs;

    protected static Internal get() {
        return internal;
    }

    protected Internal(Context context) {
        sharedPrefs = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        loadDefaultsFromMetadata(context);
    }

    // TODO put in secure storage
    protected void setToken(ResponsesPublic.OAuthResponse token) {
        sharedPrefs.edit().putString(TOKEN_KEY, Utils.gson.toJson(token)).apply();
    }

    // TODO get out of secure storage
    protected static ResponsesPublic.OAuthResponse getToken() {
        return Utils.gson.fromJson(sharedPrefs.getString(TOKEN_KEY, null), ResponsesPublic.OAuthResponse.class);
    }

    protected String getClientId() {
        return clientId;
    }

    protected static void init(Context context) {
        internal = new Internal(context);
    }

    // do some stuff to reset the session, like clear the token
    protected void reset() {
        sharedPrefs.edit().remove(TOKEN_KEY).apply();
    }

    protected static boolean hasToken() {
        return getToken() != null;
    }

    protected String getClientSecret() {
        return clientSecret;
    }


    /**
     * Gets client ID from application metadata
     * @param context a context
     * @return success or failure
     */
    protected boolean loadDefaultsFromMetadata(Context context) {
        // return cached value if we have one
        if (clientId != null) {
            return true;
        }

        // fail out if context is null
        if (context == null) {
            return false;
        }

        ApplicationInfo ai = null;
        try {
            ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        if (ai == null || ai.metaData == null) {
            return false;
        }

        clientId = ai.metaData.getString(CLIENT_ID_METADATA);

        return true;
    }


    /**
     * Get this app's signature(s) (a hash of the keystore (release or debug))
     * This gets sent to the server and allows us to validate that the developer
     * we think signed this application, actually signed this application.
     * @param librarySignature Gets the signature of the Automatic SDK library instead of the 3rd party library (used for validating Service Binder access)
     * Hash is generated by the following code:
     * (Mac)        keytool -exportcert -alias <RELEASE_KEY_ALIAS> -keystore <RELEASE_KEY_PATH> | openssl sha1 -binary | openssl base64
     * (Windows)    keytool -exportcert -alias <RELEASE_KEY_ALIAS> -keystore <RELEASE_KEY_PATH> | openssl sha1 -binary | openssl base64
     */
    @SuppressLint("PackageManagerGetSignatures")    // this vulnerability does not apply to us
    protected String getApplicationSignature(Context context, Boolean librarySignature) {
        if (context == null) {
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            return null;
        }

        String packageName;

        if (librarySignature) {
            packageName = "com.automatic.sdk";
        }else {
            packageName = context.getPackageName();
        }

        PackageInfo pInfo;
        try {
            pInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }

        Signature[] signatures = pInfo.signatures;
        if (signatures == null || signatures.length == 0) {
            return null;
        }

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        md.update(pInfo.signatures[0].toByteArray());
        return Base64.encodeToString(md.digest(), Base64.URL_SAFE | Base64.NO_PADDING);
    }

    protected static boolean isInitialized() {
        return internal != null;
    }
}
