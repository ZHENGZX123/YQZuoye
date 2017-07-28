package cn.kiway.yqyd.utils;

import android.os.Environment;

/**
 * Created by Administrator on 2017/5/18.
 */

public class IContants {
    public final static String userName = "userName";
    public final static String passWord = "passWord";
    public final static String schoolCode = "schoolCode";
    public final static String token = "token";

    //html资源目录
    public final static String baseUrl = "/yqyd/yqyd/index/index.html";
    public final static String accetsUrl = "file:///android_asset/yqyd(teacher)" + baseUrl + "?token=";
    public final static String sdUrl = Environment.getExternalStorageDirectory() + "/yqyd" + baseUrl;
    public final static String fileSdUrl = "file:///" + sdUrl + "?token=";
    public final static String replaceUrl="http://yqyd.qgjydd.com/yqyd/static/version/teacher.zip";

}
