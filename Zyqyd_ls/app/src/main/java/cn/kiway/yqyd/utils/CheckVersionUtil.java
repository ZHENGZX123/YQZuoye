package cn.kiway.yqyd.utils;

import android.content.Context;
import android.os.Environment;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/5/16.
 */

public class CheckVersionUtil {

    public static String yqydPack = "yqyd";

    /**
     * 读取asset下面的版本号
     */
    public static String readFromAsset(Context context) {
        String res = "";
        try {
            InputStream in = context.getResources().getAssets().open("yqyd(teacher)" +
                    "/yqyd/version/zip_ls.json");
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            JSONObject da = new JSONObject(new String(buffer, "UTF-8"));
            res = da.optString("zipCode");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //读取sdk下面的版本号
    public static String readFileSdcard() {
        String res = "";
        if (new File(Environment.getExternalStorageDirectory() + "/yqyd/yqyd/version/zip_ls.json").exists()) {
            try {
                FileInputStream fin = new FileInputStream(Environment.getExternalStorageDirectory() +
                        "/yqyd/yqyd/version/zip_ls.json");
                int length = fin.available();
                byte[] buffer = new byte[length];
                fin.read(buffer);
                JSONObject da = new JSONObject(new String(buffer, "UTF-8"));
                res = da.optString("zipCode");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public static int returnVersion(String first, String old) {//比较大小
        return first.compareTo(old);
    }

    /**
     * 检查assest下与本地版本号  哪个大用哪个
     */
    public static String returnPkVersion(Context context) {
        String assetsVersion = readFromAsset(context);
        String sdkVersion = readFileSdcard();
        if (!assetsVersion.equals("") && !sdkVersion.equals("")) {//都不为空的时候比较大小
            if (returnVersion(assetsVersion, sdkVersion) > 0)
                return assetsVersion;
            else
                return sdkVersion;
        } else {//一方为空的时候，取不为空的
            if (assetsVersion.equals("") && !sdkVersion.equals(""))
                return sdkVersion;
            else if (!assetsVersion.equals("") && sdkVersion.equals(""))
                return assetsVersion;
        }
        return "0.0.0";
    }

    public static String getAppInfo(Context context) {
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            return versionName;
        } catch (Exception e) {
        }
        return null;
    }
}
