package cn.kiway.yqyd.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.kiway.yqyd.App;
import cn.kiway.yqyd.R;
import cn.kiway.yqyd.utils.IContants;
import cn.kiway.yqyd.utils.Logger;
import cn.kiway.yqyd.utils.Md5Util;
import cn.kiway.yqyd.utils.SharedPreferencesUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static cn.kiway.yqyd.utils.HttpUploadFile.loginUrl;

public class LoginActivity extends Activity implements Callback {
    private App app;
    private EditText userName, password;
    private String type;
    private TextView error;
    private Button loadButton;
    private RadioButton rb1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        app = (App) getApplicationContext();
        initView();
    }

    void initView() {
        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        error = (TextView) findViewById(R.id.error);
        loadButton = (Button) findViewById(R.id.loadbutton);
        rb1 = (RadioButton) findViewById(R.id.rb1);
    }

    public void clickLogin(View v) {
        error.setText("");
        Md5Util.hideKeyboard(this);
        if (userName.getText().length() <= 0) {
            error.setText("请输入用户名");
            return;
        }
        if (password.getText().length() <= 0) {
            error.setText("请输入密码");
            return;
        }
        if (rb1.isChecked())
            type = "1";
        else
            type = "3";
        loadButton.setText("登录中...");
        loading(userName.getText().toString(), password.getText().toString());
    }

    void loading(String userName, String password) {
        Request request = new Request.Builder()
                .url(loginUrl.replace("{userName}", userName).replace("{type}", type) + Md5Util.Md5(password))
                .build();
        Call call = app.mOkHttpClient.newCall(request);
        call.enqueue(this);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadButton.setText("登录");
                error.setText("登录失败");
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            final JSONObject data = new JSONObject(response.body().string());
            Logger.log(":::::::::::::::::::" + data);
            if (data.optInt("StatusCode") == 200) {
                SharedPreferencesUtil.save(this, IContants.userName, userName.getText().toString());
                SharedPreferencesUtil.save(this, IContants.passWord, Md5Util.Md5(password.getText().toString()));
                SharedPreferencesUtil.save(this, IContants.type, type);
                startActivity(new Intent(this, WebViewActivity2.class).putExtra(IContants.token, userName.getText()
                        .toString
                        ()));
                finish();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadButton.setText("登录");
                        error.setText("登录失败");
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
