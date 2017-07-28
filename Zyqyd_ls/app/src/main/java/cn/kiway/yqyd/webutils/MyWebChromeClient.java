package cn.kiway.yqyd.webutils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import cn.kiway.yqyd.activity.WebViewActivity2;

/**
 * Created by Administrator on 2017/5/31.
 */

public class MyWebChromeClient extends WebChromeClient {
    WebViewActivity2 activity2;

    boolean isAnimStart = false;
    int currentProgress;

    public MyWebChromeClient(WebViewActivity2 activity2) {
        this.activity2 = activity2;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        currentProgress = activity2.mProgressBar.getProgress();
        if (newProgress >= 100 && !isAnimStart) {
            // 防止调用多次动画
            isAnimStart = true;
            activity2.mProgressBar.setProgress(newProgress);
            // 开启属性动画让进度条平滑消失
            startDismissAnimation(activity2.mProgressBar.getProgress());
        } else {
            // 开启属性动画让进度条平滑递增
            startProgressAnimation(newProgress);
        }
    }

    /**
     * progressBar递增动画
     */
    private void startProgressAnimation(int newProgress) {
        ObjectAnimator animator = ObjectAnimator.ofInt(activity2.mProgressBar, "progress", currentProgress,
                newProgress);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    /**
     * progressBar消失动画
     */
    private void startDismissAnimation(final int progress) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(activity2.mProgressBar, "alpha", 1.0f, 0.0f);
        anim.setDuration(1500);  // 动画时长
        anim.setInterpolator(new DecelerateInterpolator());     // 减速
        // 关键, 添加动画进度监听器
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();      // 0.0f ~ 1.0f
                int offset = 100 - progress;
                activity2.mProgressBar.setProgress((int) (progress + offset * fraction));
            }
        });

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束
                activity2.mProgressBar.setProgress(0);
                activity2.mProgressBar.setVisibility(View.GONE);
                isAnimStart = false;
            }
        });
        anim.start();
    }
}
