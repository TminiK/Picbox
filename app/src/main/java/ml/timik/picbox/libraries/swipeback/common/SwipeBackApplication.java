package ml.timik.picbox.libraries.swipeback.common;

import android.app.Application;

/**
 * Created by fhf11991 on 2016/7/18.
 */

public class SwipeBackApplication extends Application {

    private ActivityLifecycleHelper mActivityLifecycleHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(mActivityLifecycleHelper = new ActivityLifecycleHelper());
    }

    public ActivityLifecycleHelper getActivityLifecycleHelper() {
        return mActivityLifecycleHelper;
    }
}
