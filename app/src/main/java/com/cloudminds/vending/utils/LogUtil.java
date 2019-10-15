package com.cloudminds.vending.utils;

import android.util.Log;

public class LogUtil {

    private static boolean mEnabled;

    private static final String TAG = "CloudVendingApp";

    public static boolean isEnabled() {
        return mEnabled;
    }

    public static void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    //==========================================================
    // 使用默认的LOGTAG
    //==========================================================

    public static void v(String msg) {
        if (mEnabled) {
            Log.v(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (mEnabled) {
            Log.d(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (mEnabled) {
            Log.i(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (mEnabled) {
            Log.w(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (mEnabled) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String msg, Throwable tr) {
        if (mEnabled) {
            Log.e(TAG, msg, tr);
        }
    }

    //==========================================================
    // 使用自定义的LOGTAG
    //==========================================================

    public static void v(String tag, String msg) {
        if (mEnabled) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (mEnabled) {
            Log.d(tag, msg);
        }
    }


    public static void i(String tag, String msg) {
        if (mEnabled) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (mEnabled) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (mEnabled) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (mEnabled) {
            Log.e(tag, msg, tr);
        }
    }
}
