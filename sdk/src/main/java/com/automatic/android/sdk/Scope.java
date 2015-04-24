package com.automatic.android.sdk;

/**
 * Created by duncancarroll on 3/31/15.
 * Copyright (c) 2015 Automatic Labs. All rights reserved.
 */
public enum Scope {
    Public("public"),
    UserProfile("user:profile"),
    UserFollow("user:follow"),
    Location("location"),
    CurrentLocation("current_location"),
    VehicleProfile("vehicle:profile"),
    VehicleEvents("vehicle:events"),
    VehicleVin("vehicle:vin"),
    Trips("trip"),
    TripEvents("trip:events"),
    Behavior("behavior"),
    AdapterBasic("adapter:basic");

    private String name;
    public static final String PREFIX = "scope:";

    Scope(String name) {
        this.name = PREFIX + name;
    }

    public String serverName() {
        return name;
    }

}
