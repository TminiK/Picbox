package ml.timik.picbox.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import java.util.ArrayList;
import java.util.List;

import ml.timik.picbox.R;
import ml.timik.picbox.beans.CollectionGroup;
import ml.timik.picbox.beans.LocalCollection;
import ml.timik.picbox.configs.Names;
import ml.timik.picbox.dataholders.DownloadTaskHolder;
import ml.timik.picbox.dataholders.FavouriteHolder;
import ml.timik.picbox.download.DownloadManager;
import ml.timik.picbox.helpers.DataBackup;
import ml.timik.picbox.helpers.DataRestore;
import ml.timik.picbox.helpers.DynamicIjkLibLoader;
import ml.timik.picbox.helpers.DynamicLibDownloader;
import ml.timik.picbox.helpers.FileHelper;
import ml.timik.picbox.helpers.Logger;
import ml.timik.picbox.helpers.UpdateManager;
import ml.timik.picbox.ui.activities.BaseActivity;
import ml.timik.picbox.ui.activities.LicenseActivity;
import ml.timik.picbox.ui.activities.PrivacyActivity;
import ml.timik.picbox.ui.preferences.LongClickPreference;
import ml.timik.picbox.utils.DensityUtil;
import ml.timik.picbox.utils.SharedPreferencesUtil;

import static android.app.Activity.RESULT_OK;
import static ml.timik.picbox.picboxApplication.mContext;

/**
 * Created by PureDark on 2016/9/25.
 */
public class SettingFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener, DirectoryChooserFragment.OnFragmentInteractionListener {
    public static final String KEY_PREF_PROXY_DETAIL = "pref_proxy_detail";
    public static final String KEY_PREF_PROXY_ENABLED = "pref_proxy_enabled";
    public static final String KEY_PREF_PROXY_REQUEST = "pref_proxy_request";
    public static final String KEY_PREF_PROXY_PICTURE = "pref_proxy_picture";
    public static final String KEY_PREF_PROXY_SERVER = "pref_proxy_server";

    public static final String KEY_PRER_VIEW_REMLASTSITE = "pref_view_rememberLastSite";
    public static final String KEY_PREF_VIEW_HIGH_RES = "pref_view_high_res";
    public static final String KEY_PREF_VIEW_PRELOAD_PAGES = "pref_view_preload_pages";
    public static final String KEY_PREF_VIEW_DIRECTION = "pref_view_direction";
    public static final String KEY_PREF_VIEW_VOLUME_FLICK = "pref_view_volume_flick";
    public static final String KEY_PREF_VIEW_ONE_PIC_GALLERY = "pref_view_one_pic_gallery";
    public static final String KEY_PREF_VIEW_ONE_HAND = "pref_view_one_hand";
    public static final String KEY_PREF_VIEW_VIDEO_PLAYER = "pref_view_video_player";

    public static final String DIREACTION_LEFT_TO_RIGHT = mContext.getResources().getStringArray(R.array.settings_view_direction_values)[0];
    public static final String DIREACTION_RIGHT_TO_LEFT = mContext.getResources().getStringArray(R.array.settings_view_direction_values)[1];
    public static final String DIREACTION_TOP_TO_BOTTOM = mContext.getResources().getStringArray(R.array.settings_view_direction_values)[2];

    public static final String VIDEO_IJKPLAYER = mContext.getResources().getStringArray(R.array.settings_view_video_player_values)[0];
    public static final String VIDEO_H5PLAYER = mContext.getResources().getStringArray(R.array.settings_view_video_player_values)[1];
    public static final String VIDEO_OTHERPLAYER = mContext.getResources().getStringArray(R.array.settings_view_video_player_values)[2];

    public static final String KEY_PREF_DOWNLOAD_HIGH_RES = "pref_download_high_res";
    public static final String KEY_PREF_DOWNLOAD_NOMEDIA = "pref_download_nomedia";
    public static final String KEY_PREF_DOWNLOAD_PATH = "pref_download_path";
    public static final String KEY_PREF_DOWNLOAD_IMPORT = "pref_download_import";

    public static final String KEY_PREF_FAVOURITE_EXPORT = "pref_favourite_export";
    public static final String KEY_PREF_FAVOURITE_IMPORT = "pref_favourite_import";

    public static final String KEY_PREF_CACHE_SIZE = "pref_cache_size";
    public static final String KEY_PREF_CACHE_CLEAN = "pref_cache_clean";

    public static final String KEY_PREF_BKRS_BACKUP = "pref_backupandrestore_backup";
    public static final String KEY_PREF_BKRS_RESTORE = "pref_backupandrestore_restore";

    public static final String KEY_PREF_LOCK_METHODS_DETAIL = "pref_lock_methods_detail";

    public static final String KEY_PREF_ABOUT_UPGRADE = "pref_about_upgrade";
    public static final String KEY_PREF_ABOUT_LICENSE = "pref_about_license";
    public static final String KEY_PREF_ABOUT_PRIVACY = "pref_about_privacy";
    public static final String KEY_PREF_ABOUT_H_VIEWER = "pref_about_h_viewer";

    public static final String KEY_PREF_MODE_R18_ENABLED = "pref_mode_r18_enabled";

    public static final String KEY_LAST_SITE_ID = "last_site_id";

    public static final String KEY_FIRST_TIME = "key_first_time";

    public static final String KEY_CUSTOM_HEADER_IMAGE = "key_custom_header_image";

    private static final int RESULT_CHOOSE_DIRECTORY = 1;

    private BaseActivity activity;
    private DirectoryChooserFragment mDialog;

    private boolean checking = false;

    //??????????????????????????????
    private boolean opened = false;

    public SettingFragment() {
    }

    @SuppressLint("ValidFragment")
    public SettingFragment(BaseActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(SharedPreferencesUtil.FILE_NAME);
        addPreferencesFromResource(R.xml.preferences);

        String downloadPath = DownloadManager.getDownloadPath();
        if (downloadPath != null) {
            String displayPath = Uri.decode(downloadPath);
            getPreferenceManager().findPreference(KEY_PREF_DOWNLOAD_PATH).setSummary(displayPath);
        }
        ListPreference listPreference = (ListPreference) getPreferenceManager().findPreference(KEY_PREF_VIEW_DIRECTION);
        CharSequence[] entries = listPreference.getEntries();
        int i = listPreference.findIndexOfValue(listPreference.getValue());
        i = (i <= 0) ? 0 : i;
        listPreference.setSummary(entries[i]);
        listPreference.setOnPreferenceChangeListener(this);
        listPreference = (ListPreference) getPreferenceManager().findPreference(KEY_PREF_VIEW_VIDEO_PLAYER);
        entries = listPreference.getEntries();
        i = listPreference.findIndexOfValue(listPreference.getValue());
        i = (i <= 0) ? 0 : i;
        listPreference.setSummary(entries[i]);
        listPreference.setOnPreferenceChangeListener(this);

        getPreferenceScreen().setOnPreferenceChangeListener(this);
        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .initialDirectory((downloadPath.startsWith("/")) ? downloadPath : DownloadManager.DEFAULT_PATH)
                .newDirectoryName("download")
                .allowNewDirectoryNameModification(true)
                .build();
        mDialog = DirectoryChooserFragment.newInstance(config);
        mDialog.setTargetFragment(this, 0);

        float size = (float) Fresco.getImagePipelineFactory().getMainFileCache().getSize() / ByteConstants.MB;
        Preference cacheCleanPreference = getPreferenceManager().findPreference(KEY_PREF_CACHE_CLEAN);
        cacheCleanPreference.setSummary(String.format("????????? %.2f MB", size));

        LongClickPreference prefDownloadPath = (LongClickPreference) getPreferenceManager().findPreference(KEY_PREF_DOWNLOAD_PATH);
        prefDownloadPath.setOnLongClickListener(v -> {
            new AlertDialog.Builder(activity)
                    .setTitle("??????????????????")
                    .setItems(new String[]{"?????????????????????", "????????????????????????"}, (dialogInterface, pos) -> {
                        if (pos == 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                            try {
                                startActivityForResult(intent, RESULT_CHOOSE_DIRECTORY);
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                                mDialog.show(getFragmentManager(), null);
                            }
                            new Handler().postDelayed(() -> {
                                if (!opened)
                                    activity.showSnackBar("???????????????????????????????????????????????????");
                            }, 1000);
                        } else if (pos == 1) {
                            mDialog.show(getFragmentManager(), null);
                        } else
                            activity.showSnackBar("???????????????????????????");
                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
            return true;
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        opened = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        opened = false;
    }

    @Override
    public void onSelectDirectory(@NonNull String path) {
        SharedPreferencesUtil.saveData(getActivity(), KEY_PREF_DOWNLOAD_PATH, Uri.encode(path));
        getPreferenceManager().findPreference(KEY_PREF_DOWNLOAD_PATH).setSummary(Uri.decode(path));
        mDialog.dismiss();
    }

    @Override
    public void onCancelChooser() {
        mDialog.dismiss();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(KEY_PREF_PROXY_SERVER)) {
            preference.setSummary((String) newValue);
        } else if (preference.getKey().equals(KEY_PREF_VIEW_DIRECTION)) {
            ListPreference directionPreference = (ListPreference) preference;
            CharSequence[] entries = directionPreference.getEntries();
            int i = directionPreference.findIndexOfValue((String) newValue);
            i = (i <= 0) ? 0 : i;
            directionPreference.setSummary(entries[i]);
        } else if (preference.getKey().equals(KEY_PREF_VIEW_VIDEO_PLAYER)) {
            ListPreference videoPlayerPreference = (ListPreference) preference;
            CharSequence[] entries = videoPlayerPreference.getEntries();
            int i = videoPlayerPreference.findIndexOfValue((String) newValue);
            i = (i <= 0) ? 0 : i;
            videoPlayerPreference.setSummary(entries[i]);
            if (VIDEO_IJKPLAYER.equals(newValue) && !DynamicIjkLibLoader.isLibrariesDownloaded()) {
                // ????????????????????????so???
                new DynamicLibDownloader(activity).checkDownloadLib();
            }
        }
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals(KEY_PREF_ABOUT_UPGRADE)) {
            //???????????????
            if (!checking)
                UpdateManager.checkUpdate(activity);
        } else if (preference.getKey().equals(KEY_PREF_BKRS_BACKUP)) {
            //??????
            new AlertDialog.Builder(activity).setTitle("?????????????")
                    .setMessage("???????????????????????????")
                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                        String backup = new DataBackup().DoBackup();
                        activity.showSnackBar(backup);
                    })
                    .setNegativeButton(getString(R.string.cancel), null).show();

        } else if (preference.getKey().equals(KEY_PREF_BKRS_RESTORE)) {
            //??????
            new AlertDialog.Builder(activity).setTitle("?????????????")
                    .setMessage("???????????????????????????????????????")
                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                        String restore = new DataRestore().DoRestore();
                        Intent intent = new Intent();
                        activity.setResult(RESULT_OK, intent);
                        Toast.makeText(activity, restore, Toast.LENGTH_LONG).show();
                        activity.finish();
                    })
                    .setNegativeButton(getString(R.string.cancel), null).show();

        } else if (preference.getKey().equals(KEY_PREF_ABOUT_LICENSE)) {
            //????????????
            Intent intent = new Intent(activity, LicenseActivity.class);
            startActivity(intent);
        } else if (preference.getKey().equals(KEY_PREF_ABOUT_PRIVACY)) {
            //???????????????
            Intent intent = new Intent(activity, PrivacyActivity.class);
            startActivity(intent);
        } else if (preference.getKey().equals(KEY_PREF_ABOUT_H_VIEWER)) {
            //??????
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.setting_content, new AboutFragment(activity));
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (preference.getKey().equals(KEY_PREF_DOWNLOAD_PATH)) {
            //????????????
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                try {
                    startActivityForResult(intent, RESULT_CHOOSE_DIRECTORY);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    mDialog.show(getFragmentManager(), null);
                }
                new Handler().postDelayed(() -> {
                    if (!opened)
                        activity.showSnackBar("???????????????????????????????????????????????????");
                }, 1000);
            } else {
                mDialog.show(getFragmentManager(), null);
            }
        } else if (preference.getKey().equals(KEY_PREF_DOWNLOAD_IMPORT)) {
            //???????????????
            new AlertDialog.Builder(activity).setTitle("?????????????????????????????????")
                    .setMessage("?????????????????????????????????????????????")
                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                        DownloadedImport();
                    })
                    .setNegativeButton(getString(R.string.cancel), null).show();
        } else if (preference.getKey().equals(KEY_PREF_FAVOURITE_EXPORT)) {
            //???????????????
            new AlertDialog.Builder(activity).setTitle("???????????????????????????")
                    .setMessage("???????????????????????????????????????")
                    .setPositiveButton("??????", (dialog, which) -> {
                        DocumentFile file = FileHelper.createFileIfNotExist(Names.favouritesname, DownloadManager.getDownloadPath(), Names.backupdirname);
                        if (file != null) {
                            FavouriteHolder holder = new FavouriteHolder(activity);
                            String json = new Gson().toJson(holder.getFavourites());
                            FileHelper.writeString(json, file);
                            holder.onDestroy();
                            activity.showSnackBar("?????????????????????");
                        } else
                            activity.showSnackBar("??????????????????????????????????????????");
                    })
                    .setNegativeButton(getString(R.string.cancel), null).show();
        } else if (preference.getKey().equals(KEY_PREF_FAVOURITE_IMPORT)) {
            //???????????????
            new AlertDialog.Builder(activity).setTitle("???????????????????????????")
                    .setMessage("??????????????????????????????????????????????????????")
                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                        String json = FileHelper.readString(Names.favouritesname, DownloadManager.getDownloadPath(), Names.backupdirname);
                        if (json == null) {
                            activity.showSnackBar("??????????????????????????????????????????");
                        } else {
                            try {
                                List<Pair<CollectionGroup, List<LocalCollection>>> favGroups =
                                        new Gson().fromJson(json, new TypeToken<ArrayList<Pair<CollectionGroup, ArrayList<LocalCollection>>>>() {
                                        }.getType());
                                FavouriteHolder holder = new FavouriteHolder(activity);
                                for (Pair<CollectionGroup, List<LocalCollection>> pair : favGroups) {
                                    Logger.d("DataStore", "" + pair.first);
                                    CollectionGroup group = holder.getGroupByTitle(pair.first.title);
                                    if (group == null) {
                                        group = pair.first;
                                        group.gid = holder.addFavGroup(group);
                                    }
                                    for (LocalCollection collection : pair.second) {
                                        collection.gid = group.gid;
                                        holder.addFavourite(collection);
                                    }
                                }
                                holder.onDestroy();
                                activity.showSnackBar("?????????????????????");
                            } catch (Exception e) {
                                e.printStackTrace();
                                activity.showSnackBar("?????????????????????");
                            }
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), null).show();
        } else if (preference.getKey().equals(KEY_PREF_CACHE_CLEAN)) {
            //??????????????????
            new AlertDialog.Builder(activity).setTitle("??????????????????????????????")
                    .setMessage("????????????????????????????????????????????????")
                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                        ImagePipeline imagePipeline = Fresco.getImagePipeline();
                        imagePipeline.clearDiskCaches();
                        activity.showSnackBar("??????????????????");
                        preference.setSummary("????????? 0.00 MB");
                    })
                    .setNegativeButton(getString(R.string.cancel), null).show();
        } else if (preference.getKey().equals(KEY_PREF_PROXY_DETAIL)) {
            //PROXY??????
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.setting_content, new ProxyFragment(activity));
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (preference.getKey().equals(KEY_PREF_LOCK_METHODS_DETAIL)) {
            //??????????????????
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.setting_content, new LockMethodFragment(activity));
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_CHOOSE_DIRECTORY) {
                Uri uriTree = data.getData();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    try {
                        getActivity().getContentResolver().takePersistableUriPermission(
                                uriTree, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
                String path = uriTree.toString();
                String displayPath = Uri.decode(path);
                SharedPreferencesUtil.saveData(getActivity(), KEY_PREF_DOWNLOAD_PATH, path);
                getPreferenceManager().findPreference(KEY_PREF_DOWNLOAD_PATH).setSummary(displayPath);
            }
        }
    }

    public void DownloadedImport() {
        // ????????????????????????
        activity.setSwipeBackEnable(false);
        // ????????????
        activity.setAllowExit(false);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_loading, null);
        TextView tvLoadingText = (TextView) view.findViewById(R.id.tv_loading_text);
        tvLoadingText.setText("???????????????????????????");
        final Dialog dialog = new AlertDialog.Builder(activity)
                .setView(view)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        //?????????????????????
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = DensityUtil.getScreenWidth(activity) - DensityUtil.dp2px(activity, 64);
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        new Thread(() -> {
            DownloadTaskHolder holder = new DownloadTaskHolder(activity);
            final int count = holder.scanPathForDownloadTask(DownloadManager.getDownloadPath());
            holder.onDestroy();
            activity.runOnUiThread(() -> {
                if (count > 0)
                    Toast.makeText(mContext, "????????????" + count + "??????????????????", Toast.LENGTH_SHORT).show();
                else if (count == 0)
                    Toast.makeText(mContext, "????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(mContext, "????????????", Toast.LENGTH_SHORT).show();
            });
            activity.setSwipeBackEnable(true);
            activity.setAllowExit(true);
            dialog.dismiss();
        }).start();
    }

}
