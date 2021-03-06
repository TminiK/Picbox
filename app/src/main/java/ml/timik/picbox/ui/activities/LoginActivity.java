package ml.timik.picbox.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ml.timik.picbox.picboxApplication;
import ml.timik.picbox.R;
import ml.timik.picbox.beans.Site;
import ml.timik.picbox.helpers.MDStatusBarCompat;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.btn_return)
    ImageView btnReturn;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.app_bar)
    AppBarLayout appbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    private Site site;

    private boolean checking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        MDStatusBarCompat.setSwipeBackToolBar(this, coordinatorLayout, appbar, toolbar);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            toolbar.setFitsSystemWindows(true);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) appbar.getLayoutParams();
            lp.height = (int) (MDStatusBarCompat.getStatusBarHeight(this) +
                    getResources().getDimension(R.dimen.tool_bar_height));
            appbar.setLayoutParams(lp);
        }

        setContainer(coordinatorLayout);
        /* ??????????????????????????? */
        setReturnButton(btnReturn);

        //?????????????????????Site??????
        if (picboxApplication.temp instanceof Site)
            site = (Site) picboxApplication.temp;

        //??????????????????????????????
        if (site == null || site.loginUrl == null || "".equals(site.loginUrl)) {
            Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTitle.setText("??????" + site.title);

        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        settings.setLoadWithOverviewMode(true);
//        settings.setUseWideViewPort(true);
        settings.setUserAgentString(getResources().getString(R.string.UA));
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                CookieManager cookieManager = CookieManager.getInstance();
                String cookies = cookieManager.getCookie(url);
                site.cookie = cookies;
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                CookieManager cookieManager = CookieManager.getInstance();
                String cookies = cookieManager.getCookie(url);
                site.cookie = cookies;
                if (checking)
                    back();
                else
                    showSnackBar("???????????????????????????????????????????????????????????????");
                super.onPageFinished(view, url);
            }
        });
        webView.loadUrl(site.loginUrl);
    }

    @OnClick(R.id.btn_return)
    void back() {
        picboxApplication.temp = site;
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @OnClick(R.id.btn_visit_index)
    void check() {
        if (checking)
            return;
        checking = true;
        showSnackBar("????????????????????????????????????????????????");
        webView.loadUrl(site.indexUrl);
    }
}
