package cn.kiway.yqyd.webutils;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.loader.GlideImageLoader;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cn.bingoogolapple.qrcode.zxingdemo.ScanActivity;
import cn.kiway.yqyd.App;
import cn.kiway.yqyd.activity.LoginActivity;
import cn.kiway.yqyd.activity.WebViewActivity2;
import cn.kiway.yqyd.utils.CheckVersionUtil;
import cn.kiway.yqyd.utils.DownLoadZip;
import cn.kiway.yqyd.utils.FileUtils;
import cn.kiway.yqyd.utils.HanderMessageWhat;
import cn.kiway.yqyd.utils.IContants;
import cn.kiway.yqyd.utils.Logger;
import cn.kiway.yqyd.utils.SharedPreferencesUtil;

import static cn.kiway.yqyd.utils.HanderMessageWhat.ResultMessage888;
import static cn.kiway.yqyd.utils.HanderMessageWhat.ResultMessage999;

/**
 * Created by Administrator on 2017/5/27.
 */

public class JsAndroidInterface {
    WebViewActivity2 webViewActivity2;
    App app;
    Handler handler;

    public JsAndroidInterface(WebViewActivity2 webViewActivity2, App app, Handler handler) {
        this.webViewActivity2 = webViewActivity2;
        this.app = app;
        this.handler = handler;
    }

    @JavascriptInterface
    public void logout() {
        //退出
        webViewActivity2.finish();
        SharedPreferencesUtil.save(webViewActivity2, IContants.userName, "");
        SharedPreferencesUtil.save(webViewActivity2, IContants.passWord, "");
        webViewActivity2.startActivity(new Intent(webViewActivity2, LoginActivity.class));
    }

    @JavascriptInterface
    public void scan() {
        //扫码的功能
        webViewActivity2.startActivityForResult(new Intent(webViewActivity2, ScanActivity.class), ResultMessage999);
    }

    @JavascriptInterface
    public String getSessionObj() {//js获取用户名
        JSONObject da = new JSONObject();
        try {
            da.put("userName", SharedPreferencesUtil.getString(webViewActivity2, IContants.userName));
            da.put("schoolCode", SharedPreferencesUtil.getString(webViewActivity2, IContants.schoolCode));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return da.toString();
    }

    @android.webkit.JavascriptInterface
    public void updateUserInfo(final String httpToken) {
        Logger.log("*******************" + httpToken);
        //imagepicker
        webViewActivity2.httpToken = httpToken;
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());// 图片加载器
        imagePicker.setSelectLimit(1);// 设置可以选择几张
        imagePicker.setMultiMode(false);// 是否为多选
        imagePicker.setCrop(true);// 是否剪裁
        imagePicker.setFocusWidth(800);// 需要剪裁的宽
        imagePicker.setFocusHeight(800);// 需要剪裁的高
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);// 方形
        imagePicker.setShowCamera(true);// 是否显示摄像
        Intent intent = new Intent(webViewActivity2, ImageGridActivity.class);
        webViewActivity2.startActivityForResult(intent, ResultMessage888);
    }

    @JavascriptInterface
    public void SoundRecording() {
        //录音上传
        // mPop.showAtLocation(rl, Gravity.CENTER, 0, 0);
    }

    @JavascriptInterface
    public void downLoadFile(String dowaloadUrl, String fileName, String fileType) {
        Logger.log("*****下载路径******" + dowaloadUrl);
        Logger.log("******文件名*****" + fileName);
        Logger.log("*****文件类型******" + fileType);

        String fileNamePath = fileName + "." + fileType.toLowerCase();
        File file = new File(FileUtils.createDocFloder(), fileNamePath);
        if (!file.exists()) {
            Message msg = handler.obtainMessage();
            msg.what = HanderMessageWhat.messageWhat4;
            msg.obj = "文件正在下载\n0%";
            handler.sendMessage(msg);
            DownLoadZip.downoalFile(dowaloadUrl, app, handler, fileNamePath);
        } else {
            try {
                FileUtils.openFile(app.getApplicationContext(), file.getAbsolutePath());
            } catch (Exception e) {
                Toast.makeText(app.getApplicationContext(), "打开失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @JavascriptInterface
    public String getVersionCode() {
        return CheckVersionUtil.getAppInfo(webViewActivity2);
    }

}
