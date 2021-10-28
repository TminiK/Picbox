package ml.timik.picbox.helpers;

import android.util.Log;

import ml.timik.picbox.picboxApplication;

/**
 * Created by PureDark on 2016/9/24.
 */

public class Logger {

    public static void d(String tag, String message) {
        if (picboxApplication.DEBUG)
            Log.d(tag, message);
    }

    public static void e(String tag, String message, Throwable e) {
        if (picboxApplication.DEBUG)
            Log.e(tag, message, e);
    }
}
