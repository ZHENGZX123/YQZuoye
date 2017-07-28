package cn.kiway.yqyd.webutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;

import cn.kiway.yqyd.activity.WebViewActivity2;
import cn.kiway.yqyd.utils.Logger;

import static cn.kiway.yqyd.utils.IContants.replaceUrl;

/**
 * Created by Administrator on 2017/5/27.
 */

public class WebViewClientRequest extends WebViewClient {
    Context context;

    public WebViewClientRequest(Context context) {
        this.context = context;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Logger.log("shouldOverrideUrlLoading***********" + url);
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        ((WebViewActivity2) context).mProgressBar.setVisibility(View.VISIBLE);
        ((WebViewActivity2) context).mProgressBar.setAlpha(1.0f);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        Logger.log("shouldInterceptRequest***********" + url);
        if (url.startsWith(replaceUrl)) {
            String mimeType = ShouldInterceptRequestUtil.getMimeType(url);
            Logger.log("尝试本地加载文件：" + url + "||mime:" + mimeType);
            InputStream is = null;
            try {
                String loginrl = url.replace(replaceUrl, "yqyd(teacher)");
                Logger.log("***********" + loginrl);
                is = ShouldInterceptRequestUtil.getIS(context, loginrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new WebResourceResponse(mimeType, "utf-8", is);
        }
        return super.shouldInterceptRequest(view, url);
//                WebResourceResponse response = super.shouldInterceptRequest(view, url);
//                Log.i("webview","load intercept request:" + url);
//                if (url != null && url.contains("zhu.ttf")) {
//                    String assertPath ="fonts/zhu.ttf";
//                    try {
//                        response = new WebResourceResponse("application/x-font-ttf","UTF8", getAssets().open
// (assertPath));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                return response;
    }

//            @Override///****/fonts/myfont.ttf  //这里的* 号是为了让它走：shouldInterceptRequest
//            public void onPageFinished(WebView view, String url) {
//                view.loadUrl("javascript:!function(){" +
//                        "s=document.createElement('style');s.innerHTML="
//                        + "\"@font-face{font-family:myhyqh;src:url('****/fonts/zhu.ttf');}*{font-family:myhyqh
// !important;}\";"
//                        + "document.getElementsByTagName('head')[0].appendChild(s);" +
//                        "document.getElementsByTagName('body')[0].style.fontFamily = \"myhyqh\";}()");
//                super.onPageFinished(view, url);
//            }
}
