package ml.timik.picbox.helpers;

import android.util.Log;

import ml.timik.picbox.HViewerApplication;

/**
 * Created by PureDark on 2016/9/24.
 */

public class Logger {

    public static void d(String tag, String message) {
        if (HViewerApplication.DEBUG)
            Log.d(tag, message);
    }

    public static void e(String tag, String message, Throwable e) {
        if (HViewerApplication.DEBUG)
            Log.e(tag, message, e);
    }
}
