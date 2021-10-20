/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package ml.timik.picbox.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;

import java.util.List;

import me.zhanghai.android.patternlock.PatternView;
import ml.timik.picbox.helpers.MDStatusBarCompat;
import ml.timik.picbox.ui.fragments.LockMethodFragment;
import ml.timik.picbox.utils.AppUtils;
import ml.timik.picbox.utils.PatternLockUtils;
import ml.timik.picbox.utils.SharedPreferencesUtil;

public class SetPatternActivity extends me.zhanghai.android.patternlock.SetPatternActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setActionBarDisplayUp(this);
        MDStatusBarCompat.setOrdinaryToolBar(this);
        setTitle("解锁图案");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AppUtils.navigateUp(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSetPattern(List<PatternView.Cell> pattern) {
        PatternLockUtils.setPattern(this, pattern);
        SharedPreferencesUtil.saveData(this, LockMethodFragment.KEY_PREF_CURR_LOCK_METHOD, "pattern");
    }
}
