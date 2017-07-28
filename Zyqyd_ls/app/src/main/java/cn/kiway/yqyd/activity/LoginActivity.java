package cn.kiway.yqyd.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.kiway.yqyd.App;
import cn.kiway.yqyd.R;
import cn.kiway.yqyd.utils.IContants;
import cn.kiway.yqyd.utils.SharedPreferencesUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static cn.kiway.yqyd.utils.HttpUploadFile.loginUrl;

public class LoginActivity extends Activity implements Callback {
    App app;
    EditText userName, password;
    TextView error;
    Button loadButton;

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
    }

    public void clickLogin(View v) {
        hideKeyboard();
        error.setText("");
        if (userName.getText().length() <= 0) {
            error.setText("请输入用户名");
            return;
        }
        if (password.getText().length() <= 0) {
            error.setText("请输入密码");
            return;
        }
        loadButton.setText("登录中...");
        loading(userName.getText().toString(), password.getText().toString());
    }

    void loading(String userName, String password) {
        Request request = new Request.Builder()
                .url(loginUrl.replace("{userName}", userName) + password)
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
            Log.d("test", "data = " + data.toString());
            if (data.optInt("errcode") == 200) {
                SharedPreferencesUtil.save(this, IContants.userName, userName.getText().toString());
                SharedPreferencesUtil.save(this, IContants.passWord, password.getText().toString());
                SharedPreferencesUtil.save(this, IContants.schoolCode, data.optString("schoolCode"));
                startActivity(new Intent(this, WebViewActivity2.class).putExtra(IContants.token, userName.getText()
                        .toString()));
                finish();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadButton.setText("登录");
                        error.setText(data.optString("errmsg"));
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏软键盘
     */
    void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
