package com.automatic.automaticsdksampleapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by duncancarroll on 4/3/15.
 */
public class Utils {


    /**
     * Wrapper for SimpleDateFormat
     * @param timestamp
     * @return
     */
    public static String getDateString(Long timestamp) {
        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
            Date dt = new Date(timestamp);
            return sdf.format(dt);
        } else {
            return "unknown";
        }
    }
}
