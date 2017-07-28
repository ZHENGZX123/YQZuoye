package cn.kiway.yqyd.utils;

import android.util.Log;

/**
 * Created by Administrator on 2017/5/8.
 */

public class Logger {
    static String packName = "cn.kiway.yqyd";
    static boolean isLog = true;

    public static void log(Object message) {
        if (isLog)
            Log.e(packName, message.toString());
    }
}
