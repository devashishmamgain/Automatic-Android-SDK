package com.automatic.android.sdk;

import retrofit.RetrofitError;

/**
 * Created by duncancarroll on 2/12/15.
 * Copyright (c) 2015 Automatic Labs. All rights reserved.
 */
public interface AutomaticLoginCallbacks {

    void onLoginSuccess();
    void onLoginFailure(RetrofitError error);

}
