package cn.kiway.yqyd.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.kiway.yqyd.App;
import cn.kiway.yqyd.R;
import cn.kiway.yqyd.dialog.LoginDialog;
import cn.kiway.yqyd.utils.CheckVersionUtil;
import cn.kiway.yqyd.utils.DownLoadZip;
import cn.kiway.yqyd.utils.FileUtils;
import cn.kiway.yqyd.utils.HanderMessageWhat;
import cn.kiway.yqyd.utils.IContants;
import cn.kiway.yqyd.utils.Logger;
import cn.kiway.yqyd.utils.SharedPreferencesUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static cn.kiway.yqyd.utils.HttpUploadFile.checkVersion;
import static cn.kiway.yqyd.utils.HttpUploadFile.loginUrl;

/**
 * Created by Administrator on 2017/5/8.
 */

public class BootActivity extends Activity implements Animation.AnimationListener, Callback {
    private ImageView Img;
    private App app;
    private LoginDialog loginDialog;
    private JSONObject da;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_boot);
        Img = (ImageView) findViewById(R.id.img);
        setAnimmation(Img);
        app = (App) getApplicationContext();
        loginDialog = new LoginDialog(this);
        loginDialog.setTitle("正在下载更新包，请稍后\n0%");
    }

    void setAnimmation(View view) {
        //动画效果参数直接定义
        Animation animation = new AlphaAnimation(0.1f, 1.0f);
        if (SharedPreferencesUtil.getString(this,IContants.userName).equals("")) {
            animation.setDuration(2000);
        } else {
            animation.setDuration(1000);
        }
        animation.setAnimationListener(this);
        view.setAnimation(animation);
    }

    public void loading() {
        final Request request = new Request.Builder()
                .url(loginUrl.replace("{userName}", SharedPreferencesUtil.getString(this, IContants.userName))
                        .replace("{type}", SharedPreferencesUtil.getString(this, IContants.type)) +
                        SharedPreferencesUtil.getString(this, IContants.passWord))
                .build();
        Call call = app.mOkHttpClient.newCall(request);
        call.enqueue(this);
    }

    void startLoadingActivity(final String showMessage) {
        if (showMessage != null && !showMessage.equals(""))
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BootActivity.this, showMessage, Toast.LENGTH_SHORT);
                }
            });
        finish();
        startActivity(new Intent(BootActivity.this, LoginActivity.class));
    }

    @Override
    public void onFailure(Call call, IOException e) {
        startLoadingActivity("登录失败，请重新登录");
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Log.e("url", call.request().url().toString());
        try {
            if (call.request().url().toString().equals(checkVersion)) {
                final JSONObject data = new JSONObject(response.body().string());
                Logger.log("检查更新：：：：：：" + data);
                if (CheckVersionUtil.returnVersion(data.optString("zipCode"), CheckVersionUtil.returnPkVersion
                        (BootActivity.this)) > 0) {
                    //需要更新
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginDialog.show();
                        }
                    });
                    da = data;
                    DownLoadZip.downoalZip(data.optString("zipUrl"), app, this, mHandler,false);
                } else {
                    //不需要更新
                    loading();
                }
            } else {
                final JSONObject data = new JSONObject(response.body().string());
                if (data.optInt("StatusCode") == 200) {
                    startActivity(new Intent(this, WebViewActivity2.class).putExtra(IContants.token,
                            SharedPreferencesUtil
                            .getString(this, IContants.userName)));
                    finish();
                } else {
                    startLoadingActivity("登录失败");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (SharedPreferencesUtil.getString(this, "userName").equals("")) {
            startLoadingActivity(null);
        } else {
            loading();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    //检查版本请求
    void checkVersion() {
        final Request request = new Request.Builder()
                .url(checkVersion)
                .build();
        Call call = app.mOkHttpClient.newCall(request);
        call.enqueue(this);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HanderMessageWhat.messageWhat1:
                    loginDialog.setTitle(msg.obj.toString());

                    break;
                case FileUtils.CompressStatus.COMPLETED:
                    loginDialog.close();
                    FileUtils.writeFileData(da.toString());//解压成功后写入当前版本号
                    Toast.makeText(BootActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    loading();
                    break;
                case FileUtils.CompressStatus.ERROR:
                    loginDialog.close();
                    loading();
                    Toast.makeText(BootActivity.this, "解压失败", Toast.LENGTH_SHORT).show();
                    break;
                case FileUtils.CompressStatus.HANDLING:
                    loginDialog.setTitle(msg.obj.toString());
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
