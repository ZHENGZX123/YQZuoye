package cn.kiway.yqyd.webutils;

import android.content.Context;
import android.webkit.MimeTypeMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Created by Administrator on 2017/5/27.
 */

public class ShouldInterceptRequestUtil {

    public static InputStream getIS(Context context, String localUrl) throws IOException {
        InputStream in = context.getAssets().open(localUrl);
        return in;
    }

    public static String getMimeType(String url) {
        String type = null;
        if (url.toLowerCase(Locale.CHINA).endsWith(".w")) {
            type = "text/html";
        } else {
            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
            if (extension != null) {
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                type = mime.getMimeTypeFromExtension(extension);
            }
        }
        return type;
    }
}
