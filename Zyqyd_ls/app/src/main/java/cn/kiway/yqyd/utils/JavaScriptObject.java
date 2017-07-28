package cn.kiway.yqyd.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.loader.GlideImageLoader;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

public class JavaScriptObject {
    private Context mContxt;

    public JavaScriptObject(Context mContxt) {
        this.mContxt = mContxt;
    }

    @JavascriptInterface
    public void toast(String txt) {
        Toast.makeText(mContxt, txt, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void pickphoto(String txt) {
        Toast.makeText(mContxt, txt, Toast.LENGTH_SHORT).show();


        //imagepicker
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());// 图片加载器
        imagePicker.setSelectLimit(1);// 设置可以选择几张
        imagePicker.setMultiMode(false);// 是否为多选
        imagePicker.setCrop(true);// 是否剪裁
        imagePicker.setFocusWidth(1000);// 需要剪裁的宽
        imagePicker.setFocusHeight(1000);// 需要剪裁的高
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);// 方形
        imagePicker.setShowCamera(true);// 是否显示摄像

        Intent intent = new Intent(this.mContxt, ImageGridActivity.class);
        ((Activity) this.mContxt).startActivityForResult(intent, 888);
    }

}
