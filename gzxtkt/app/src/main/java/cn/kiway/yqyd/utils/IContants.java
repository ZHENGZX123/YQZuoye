package cn.kiway.yqyd.utils;

import android.os.Environment;

/**
 * Created by Administrator on 2017/5/18.
 */

public class IContants {
    public final static String userName = "userName";
    public final static String passWord = "passWord";
    public final static String type = "type";
    public final static String token = "token";
    public final static String url = "url";


    //html资源目录
    public final static String baseUrl = "/gzxtkt/wx/index.html";
    public final static String accetsUrl = "file:///android_asset/txkt_teacher" + baseUrl;
    public final static String sdUrl = Environment.getExternalStorageDirectory() + "/gzxtkt" + baseUrl;
    public final static String fileSdUrl = "file:///" + sdUrl;
    public final static String ceshiUrl = "http://yqyd.qgjydd.com/yqyd/static/version/teacher.zip" + baseUrl;
}
