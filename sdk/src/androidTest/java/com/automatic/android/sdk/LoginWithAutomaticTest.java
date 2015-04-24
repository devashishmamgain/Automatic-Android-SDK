package com.automatic.android.sdk;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

/**
 * Created by duncancarroll on 2/12/15.
 * Copyright (c) 2015 Automatic Labs. All rights reserved.
 */
public class LoginWithAutomaticTest extends ActivityInstrumentationTestCase2<Activity> {

    Automatic api;

    public LoginWithAutomaticTest() {
        super(Activity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @LargeTest
    public void testOAuth() {
        //api.loginWithAutomatic();
    }

}
