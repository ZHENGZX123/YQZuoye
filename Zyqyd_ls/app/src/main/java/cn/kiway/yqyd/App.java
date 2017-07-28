package cn.kiway.yqyd;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017/5/10.
 */

public class App extends Application {
    public OkHttpClient mOkHttpClient =
            new OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)//设置读取超时时间
                    .writeTimeout(30, TimeUnit.SECONDS)//设置写的超时时间
                    .connectTimeout(30, TimeUnit.SECONDS)//设置连接超时时间
                    .build();
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }
}
