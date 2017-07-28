package cn.kiway.yqyd.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017/5/15.
 */

public class Md5Util {
    public static String Md5(String str) {
        String result = "";
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update((str).getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte b[] = md5.digest();
        int i;
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0) {
                i += 256;
            }
            if (i < 16) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(i));
        }
        result = buf.toString();
        System.out.println("result = " + result);
        return result;
    }

    /**
     * 隐藏软键盘
     *
     * @param act
     */
    public static void hideKeyboard(Dialog dialog, Activity act) {
        try {
            InputMethodManager imm = (InputMethodManager) act
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = dialog.getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param act
     */
    public static void hideKeyboard(Activity act) {
        try {
            InputMethodManager imm = (InputMethodManager) act
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = act.getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
