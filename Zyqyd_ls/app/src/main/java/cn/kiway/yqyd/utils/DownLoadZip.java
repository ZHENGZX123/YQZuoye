package cn.kiway.yqyd.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.kiway.yqyd.App;
import cn.kiway.yqyd.activity.BootActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/16.
 */

public class DownLoadZip {
    public static void downoalZip(String pptUrl, App app, final BootActivity activity, final Handler mHandler, final
    boolean isApk) {
        Request request = new Request.Builder().url(pptUrl).build();
        app.mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.loading();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file;
                    if (isApk)
                        file = new File(FileUtils.createZipFloder(), "yqyd.apk");
                    else
                        file = new File(FileUtils.createZipFloder(), "yqyd.zip");
                    file.delete();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        Log.d("h_bl", "progress=" + progress);
                        Message msg = mHandler.obtainMessage();
                        msg.what = HanderMessageWhat.messageWhat1;
                        msg.obj = "正在下载更新包,请稍后\n" + progress + "%";
                        mHandler.sendMessage(msg);
                    }
                    fos.flush();
                    Log.d("h_bl", "文件下载成功");
                    Message msg = mHandler.obtainMessage();
                    msg.what = HanderMessageWhat.messageWhat1;
                    msg.obj = "解压更新包";
                    mHandler.sendMessage(msg);
                    if (isApk) {//启动安装
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file),
                                "application/vnd.android.package-archive");
                        activity.startActivity(intent);
                        System.exit(0);//正常退出App
                    } else {//启动解压
                        FileUtils.unZipFileWithProgress(FileUtils.createZipFloder() + "/yqyd.zip", FileUtils
                                .createZipFloder() + "/yqyd", mHandler, true);
                    }
                } catch (Exception e) {
                    Log.d("h_bl", "文件下载失败");
                    Message msg = mHandler.obtainMessage();
                    msg.what = HanderMessageWhat.messageWhat1;
                    msg.obj = "文件下载失败,取消更新，重新登录";
                    mHandler.sendMessage(msg);
                    activity.loading();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    public static void downoalFile(final String fileUrl, final App app, final Handler mHandler, final String
            fileNamePath) {
        Request request = new Request.Builder().url(fileUrl).build();
        app.mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = mHandler.obtainMessage();
                msg.what = HanderMessageWhat.messageWhat2;
                msg.obj = "文件下载失败";
                mHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file;
                    file = new File(FileUtils.createDocFloder(), fileNamePath);
                    file.delete();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        Log.d("h_bl", "progress=" + progress);
                        Message msg = mHandler.obtainMessage();
                        msg.what = HanderMessageWhat.messageWhat3;
                        msg.obj = "正在下载,请稍后\n" + progress + "%";
                        mHandler.sendMessage(msg);
                    }
                    fos.flush();
                    Log.d("h_bl", "文件下载成功");
                    Message msg = mHandler.obtainMessage();
                    msg.what = HanderMessageWhat.messageWhat2;
                    msg.obj = "文件下载成功";
                    mHandler.sendMessage(msg);
                    //下载成功后做什么
                    try {
                        FileUtils.openFile(app.getApplicationContext(), file.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(app.getApplicationContext(),"打开失败",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.d("h_bl", "文件下载失败");
                    Message msg = mHandler.obtainMessage();
                    msg.what = HanderMessageWhat.messageWhat2;
                    msg.obj = "文件下载失败";
                    mHandler.sendMessage(msg);
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }
}
