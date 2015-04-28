package com.automatic.android.sdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;

import retrofit.RetrofitError;


/**
 * Created by duncancarroll on 3/18/15.
 * Copyright (c) 2015 Automatic Labs. All rights reserved.
 */
public class LoginButton extends Button {

    private static final String TAG = LoginButton.class.getName();
    private AutomaticLoginCallbacks mCallbacks;
    private String loginText;
    private String logoutText;

    public LoginButton(Context context) {
        super(context);
        setStyles(context, null);
    }

    public LoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setStyles(context, attrs);
    }

    public LoginButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setStyles(context, attrs);
    }

    protected void setStyles(Context context, AttributeSet attrs) {

        // background color is fixed
        setBackgroundResource(R.drawable.button_background);
        // minimum width is fixed
        setMinimumWidth((int) getResources().getDimension(R.dimen.button_min_width));
        if (attrs == null || attrs.getStyleAttribute() == 0) {
            // set defaults
            setGravity(Gravity.CENTER);
            setTextColor(getResources().getColor(R.color.button_text_color));
            setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.button_text_size));
            setTypeface(Typeface.DEFAULT_BOLD);
            if (isInEditMode()) {
                // cannot use a drawable in edit mode, so setting the background color instead
                // of a background resource.
                setBackgroundColor(getResources().getColor(R.color.button_background_color));
                // hardcoding in edit mode as getResources().getString() doesn't seem to work in IntelliJ
                loginText = "Log in with Automatic";
                setText(loginText);
            } else {
                setCompoundDrawablesWithIntrinsicBounds(R.mipmap.automatic_button_white, 0, 0, 0);
                setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.button_logo_drawable_padding));
                setPadding(getResources().getDimensionPixelSize(R.dimen.button_padding_left),
                        getResources().getDimensionPixelSize(R.dimen.button_padding_top),
                        getResources().getDimensionPixelSize(R.dimen.button_padding_right),
                        getResources().getDimensionPixelSize(R.dimen.button_padding_bottom));
                if (Internal.isInitialized() && Internal.hasToken()) {
                    loginText = getResources().getString(R.string.button_text_connected);
                    setBackgroundResource(R.drawable.button_bg_state_connected);
                }else {
                    loginText = getResources().getString(R.string.button_text_login);
                }

                setText(loginText);
            }
        }
        parseAttributes(attrs);
    }

    protected void updateStyles() {
        // logged in
        if (Internal.isInitialized() && Internal.get().hasToken()) {
            loginText = getResources().getString(R.string.button_text_connected);
            setBackgroundResource(R.drawable.button_bg_state_connected);
        } else {
            // logged out
            loginText = getResources().getString(R.string.button_text_login);
            setBackgroundResource(R.drawable.button_background);
        }
        setText(loginText);

    }

    // allow users to set their own Login / Logout text
    private void parseAttributes(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.login_button_view);
            loginText = a.getString(R.styleable.login_button_view_login_text);
            logoutText = a.getString(R.styleable.login_button_view_logout_text);
            a.recycle();
        }
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * Add a custom success / failure handler here (if desired), otherwise the SDK will handle it for you.
     * @param callback callback to add
     */
    public void setOnLoginResultCallback(AutomaticLoginCallbacks callback) {
        mCallbacks = callback;
    }

    protected boolean invokeCallbacks(Boolean success) {
        return invokeCallbacks(success, null);
    }

    protected boolean invokeCallbacks(Boolean success, RetrofitError error) {
        if (mCallbacks == null) {
            return false;
        }
        if (success) {
            mCallbacks.onLoginSuccess();
        }else {
            mCallbacks.onLoginFailure(error);
        }
        return true;
    }


    protected void showLogoutConfirmDialog(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Log Out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        Automatic.logout();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

}
