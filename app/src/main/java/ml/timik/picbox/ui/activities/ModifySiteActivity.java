package ml.timik.picbox.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.provider.DocumentFile;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ml.timik.picbox.picboxApplication;
import ml.timik.picbox.R;
import ml.timik.picbox.beans.Site;
import ml.timik.picbox.configs.PasteEEConfig;
import ml.timik.picbox.dataholders.SiteHolder;
import ml.timik.picbox.download.DownloadManager;
import ml.timik.picbox.helpers.FileHelper;
import ml.timik.picbox.helpers.MDStatusBarCompat;
import ml.timik.picbox.helpers.SitePropViewHolder;
import ml.timik.picbox.http.HViewerHttpClient;
import ml.timik.picbox.utils.QRCodeUtil;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ModifySiteActivity extends BaseActivity {

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edittext_container)
    RelativeLayout edittextContainer;
    @BindView(R.id.view_share_site_qr_code)
    View viewShareSiteQrCode;
    @BindView(R.id.view_share_site_json)
    View viewShareSiteJson;
    @BindView(R.id.view_site_details)
    View viewSiteDetails;
    @BindView(R.id.btn_return)
    ImageView btnReturn;

    @BindView(R.id.input_site)
    MaterialEditText inputSite;

    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;

    private SitePropViewHolder holder;

    private Site site;

    private boolean isPosting = false;

    private SiteHolder siteHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_site);
        ButterKnife.bind(this);
        MDStatusBarCompat.setSwipeBackToolBar(this, coordinatorLayout, appbar, toolbar);

        setSupportActionBar(toolbar);
        setContainer(coordinatorLayout);

        if ((Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) edittextContainer.getLayoutParams();
            lp.topMargin += MDStatusBarCompat.getStatusBarHeight(this);
            edittextContainer.setLayoutParams(lp);
        }

        /* ??????????????????????????? */
        setReturnButton(btnReturn);

        //?????????????????????Site??????
        if (picboxApplication.temp instanceof Site)
            site = (Site) picboxApplication.temp;

        //??????????????????????????????
        if (site == null) {
            finish();
            return;
        }

        siteHolder = new SiteHolder(this);
        holder = new SitePropViewHolder(viewSiteDetails, siteHolder.getGroups());
        holder.fillSitePropEditText(site);
    }

    @OnClick(R.id.btn_return)
    void back() {
        onBackPressed();
    }


    @OnClick(R.id.btn_site_json)
    void showSiteJson() {
        Site newSite = holder.fromEditTextToSite(false);
        if (newSite == null) {
            showSnackBar("????????????????????????????????????");
            return;
        }
        newSite.cookie = null;
        inputSite.setText(new Gson().toJson(newSite));
        switchBetweenShareAndDetail(viewShareSiteJson);
    }

    @OnClick(R.id.btn_site_qr_code)
    void generateQrCode() {
        if (isPosting) return;
        Site newSite = holder.fromEditTextToSite(false);
        if (newSite == null) {
            showSnackBar("????????????????????????????????????");
            return;
        }
        newSite.cookie = null;
        final String jsonStr = new Gson().toJson(newSite);

        RequestBody requestBody = new FormBody.Builder()
                .add("key", PasteEEConfig.appkey)
                .add("description", "")
                .add("paste", jsonStr)
                .add("format", "json")
                .build();

        showSnackBar("?????????????????????????????????");
        isPosting = true;
        HViewerHttpClient.get(PasteEEConfig.apiUrl, false, null, true, requestBody, new HViewerHttpClient.OnResponseListener() {
            @Override
            public void onSuccess(String contentType, Object result) {
                isPosting = false;
                final String url;
                try {
                    JsonObject jsonObject = new JsonParser().parse((String) result).getAsJsonObject();
                    if (jsonObject.has("status") && "success".equals(jsonObject.get("status").getAsString())) {
                        url = jsonObject.get("paste").getAsJsonObject().get("raw").getAsString();
                    } else {
                        onFailure(null);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(null);
                    return;
                }


                if (url == null)
                    return;
                //???????????????????????????????????????????????????????????????????????????????????????
                new Thread(() -> {
                    FileHelper.createFileIfNotExist("temp", DownloadManager.getDownloadPath());
                    final boolean success = QRCodeUtil.createQRImage(url, 300, 300,
                            BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher),
                            FileHelper.getFileOutputSteam("temp", DownloadManager.getDownloadPath()));

                    runOnUiThread(() -> {
                        if (success) {
                            switchBetweenShareAndDetail(viewShareSiteQrCode);
                            ivQrCode.setImageBitmap(BitmapFactory.decodeStream(FileHelper.getFileInputSteam("temp", DownloadManager.getDownloadPath())));
                        } else {
                            onFailure(null);
                        }
                    });
                }).start();
            }

            @Override
            public void onFailure(HViewerHttpClient.HttpError error) {
                isPosting = false;
                showSnackBar("??????????????????????????????????????????");
            }
        });
    }

    @OnClick(R.id.btn_copy_json)
    void copyJson() {
        ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        myClipboard.setPrimaryClip(ClipData.newPlainText("json", inputSite.getText()));
        showSnackBar("?????????????????????");
    }

    @OnClick(R.id.btn_save_qr_code)
    void saveQrCode() {
        Bitmap bitmap = ((BitmapDrawable) ivQrCode.getDrawable()).getBitmap();
        try {
            String fileName = FileHelper.filenameFilter(site.title) + ".jpg";
            DocumentFile documentFile = FileHelper.createFileIfNotExist(fileName, DownloadManager.getDownloadPath(), "QrCodes");
            FileHelper.saveBitmapToFile(bitmap, documentFile);
            showSnackBar("??????????????????????????????????????????");
        } catch (IOException e) {
            e.printStackTrace();
            showSnackBar("?????????????????????");
        }
    }

    @OnClick(R.id.fab_submit)
    void submit() {
        Site newSite = holder.fromEditTextToSite(false);
        if (newSite == null) {
            showSnackBar("????????????????????????????????????");
            return;
        }
        if (newSite.gid == 0) {
            showSnackBar("????????????????????????????????????????????????");
            return;
        }

        newSite.sid = site.sid;
        newSite.index = site.index;
        picboxApplication.temp = newSite;
        siteHolder.updateSite(newSite);

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }


    void switchBetweenShareAndDetail(View view) {
        if (viewSiteDetails.getVisibility() == View.GONE) {
            viewSiteDetails.setVisibility(View.VISIBLE);
            viewShareSiteQrCode.setVisibility(View.GONE);
            viewShareSiteJson.setVisibility(View.GONE);
        } else {
            viewSiteDetails.setVisibility(View.GONE);
            viewShareSiteQrCode.setVisibility(View.GONE);
            viewShareSiteJson.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        }
    }

}
