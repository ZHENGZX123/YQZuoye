package cn.kiway.yqyd.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.loader.GlideImageLoader;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import cn.kiway.yqyd.App;
import cn.kiway.yqyd.R;
import cn.kiway.yqyd.dialog.LoginDialog;
import cn.kiway.yqyd.utils.HanderMessageWhat;
import cn.kiway.yqyd.utils.HttpUploadFile;
import cn.kiway.yqyd.utils.IContants;
import cn.kiway.yqyd.utils.Logger;
import cn.kiway.yqyd.utils.SharedPreferencesUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static cn.kiway.yqyd.utils.HanderMessageWhat.ResultMessage777;
import static cn.kiway.yqyd.utils.HanderMessageWhat.ResultMessage888;
import static cn.kiway.yqyd.utils.HanderMessageWhat.ResultMessage999;

public class WebViewActivity2 extends Activity implements Callback {

    private WebView wv;
    private LoginDialog loginDialog;
    private boolean isChangeUserPic;
    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview2);
        wv = (WebView) findViewById(R.id.wv);
        loginDialog = new LoginDialog(this);
        app = (App) getApplicationContext();
        initData();
        load();
    }


    private void initData() {
        //跨域问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            wv.getSettings().setAllowUniversalAccessFromFileURLs(true);
        } else {
            try {
                Class<?> clazz = wv.getSettings().getClass();
                Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(wv.getSettings(), true);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setLoadWithOverviewMode(true);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:"))
                    call(url);
                else
                    view.loadUrl(url);
                return true;
            }
        });
        wv.setVerticalScrollBarEnabled(false);
        wv.setWebChromeClient(new WebChromeClient());
        wv.addJavascriptInterface(new JsAndroidIntetface(), "js");
    }

    private void load() {
        //String token = getIntent().getStringExtra("token");
        //  wv.loadUrl("file:///android_asset/txkt_teacher/yqyd/yqyd/index/index.html?token=" + token);
        wv.loadUrl(IContants.accetsUrl);
    }


    long time = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("test", "click back = " + wv.getUrl());
            String url = wv.getUrl();
            String page = url.substring(url.lastIndexOf("/") + 1);
            //Toast.makeText(this, page, Toast.LENGTH_SHORT).show();
            if (page.contains(IContants.accetsUrl) || page
                    .endsWith("sc") || page.endsWith("main") || page.endsWith("/class/myclass")) {
                long t = System.currentTimeMillis();
                if (t - time >= 2000) {
                    time = t;
                    Toast.makeText(this, "再按一下退出", Toast.LENGTH_SHORT).show();
                } else
                    finish();
                return true;
            } else {
                if (wv.canGoBack()) {
                    wv.goBack();//返回上一页面
                    return true;
                } else {
                    long t = System.currentTimeMillis();
                    if (t - time >= 2000) {
                        time = t;
                        Toast.makeText(this, "再按一下退出", Toast.LENGTH_SHORT).show();
                    } else
                        finish();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == ResultMessage888) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker
                        .EXTRA_RESULT_ITEMS);
                uploadUserPic(images.get(0).path, HttpUploadFile.FileType
                        .Image);
            } else if (data != null && requestCode == ResultMessage777) {
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null,
                        null, null);
                cursor.moveToFirst();
                String v_path = cursor.getString(1); // 图片文件路径
                String v_size = cursor.getString(2); // 图片大小
                String v_name = cursor.getString(3); // 图片文件名
                Logger.log("v_path=" + v_path);
                Logger.log("v_size=" + v_size);
                Logger.log("v_name=" + v_name);
                Toast.makeText(this, "获取视频", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "获取失败", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == ResultMessage999 && data != null) { //扫描二维码返回
            String result = data.getStringExtra("result");
            Toast.makeText(WebViewActivity2.this, result, Toast.LENGTH_SHORT).show();
        }
    }

    void uploadUserPic(String fileName, String fileType) {
        if (loginDialog != null)
            loginDialog.show();
        try {
            app.mOkHttpClient.newCall(HttpUploadFile.returnUploadImgRequser(new File(fileName), fileType)).enqueue
                    (this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
    }


    public class JsAndroidIntetface {
        public JsAndroidIntetface() {
        }

        @JavascriptInterface
        public void logout() {
            //退出
            SharedPreferencesUtil.save(WebViewActivity2.this, IContants.userName, "");
            SharedPreferencesUtil.save(WebViewActivity2.this, IContants.passWord, "");
            startActivity(new Intent(WebViewActivity2.this, LoginActivity.class));
            finish();
        }

        @JavascriptInterface
        public void choosePhotos(String isChangeUserPic) {
            //imagepicker
            WebViewActivity2.this.isChangeUserPic = Boolean.parseBoolean(isChangeUserPic);
            ImagePicker imagePicker = ImagePicker.getInstance();
            imagePicker.setImageLoader(new GlideImageLoader());// 图片加载器
            if (Boolean.parseBoolean(isChangeUserPic)) {
                imagePicker.setSelectLimit(1);// 设置可以选择几张
                imagePicker.setMultiMode(false);// 是否为多选
                imagePicker.setCrop(true);// 是否剪裁
                imagePicker.setFocusWidth(800);// 需要剪裁的宽
                imagePicker.setFocusHeight(800);// 需要剪裁的高
                imagePicker.setStyle(CropImageView.Style.RECTANGLE);// 方形
                imagePicker.setShowCamera(true);// 是否显示摄像
            }
            Intent intent = new Intent(WebViewActivity2.this, ImageGridActivity.class);
            WebViewActivity2.this.startActivityForResult(intent, ResultMessage888);
        }

        @JavascriptInterface
        public void playVideo(String url) {
            Intent intent = new Intent(WebViewActivity2.this, VideoActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        }

        @JavascriptInterface
        public void chooseVideo() {
            Intent intent = new Intent();
            intent.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, ResultMessage777);
        }

        @JavascriptInterface
        public void showPhoto(ArrayList<String> list) {
        }
    }


    public void call(String phone) {
        //打电话
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(phone));
        startActivity(intent);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HanderMessageWhat.messageWhat1:
                    if (loginDialog != null)
                        loginDialog.close();
                    wv.loadUrl((String) msg.obj);
                    break;
                case HanderMessageWhat.messageWhat2:
                    if (loginDialog != null)
                        loginDialog.close();
                    Toast.makeText(WebViewActivity2.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}