package cn.kiway.yqyd.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.kiway.yqyd.R;
import cn.kiway.yqyd.utils.LVCircularRing;


public class LoginDialog extends Dialog implements OnDismissListener {
    LVCircularRing mLoadingView;
    Dialog mLoadingDialog;
    TextView loadingText;
    Context context;

    @SuppressLint("InflateParams")
    public LoginDialog(Context context) {
        super(context);
        this.context=context;
        View view = LayoutInflater.from(context).inflate(R.layout.login_dialog,
                null);
        // 获取整个布局
        LinearLayout layout = (LinearLayout) view
                .findViewById(R.id.dialog_view);
        // 页面中的LoadingView
        mLoadingView = (LVCircularRing) view.findViewById(R.id.lv_circularring);
        // 页面中显示文本
        loadingText = (TextView) view.findViewById(R.id.loading_text);
        // 显示文本
        // 显示文本
        loadingText.setText("上传中，请稍后");
        // 创建自定义样式的Dialog
        mLoadingDialog = new Dialog(context, R.style.loading_dialog);
        mLoadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        setOnDismissListener(this);
        setCancelable(false);
        mLoadingDialog.setCancelable(false);
    }

    public void setTitle(String string) {
        loadingText.setText(string);
    }

    public void show() {
        mLoadingDialog.show();
        mLoadingView.startAnim();
    }

    public void close() {
        if (mLoadingDialog != null) {
            mLoadingView.stopAnim();
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mLoadingDialog != null) {
            mLoadingView.stopAnim();
            mLoadingDialog.dismiss();
            //mLoadingDialog = null;
        }
    }
}
