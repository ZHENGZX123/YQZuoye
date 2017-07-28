package cn.kiway.yqyd.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.MediaController;
import android.widget.VideoView;

import cn.kiway.yqyd.R;

/**
 * Created by Administrator on 2017/5/17.
 */

public class VideoActivity extends Activity {
    VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoView = (VideoView) findViewById(R.id.videoView);
        MediaController mc = new MediaController(this);//Video是我类名，是你当前的类
        videoView.setMediaController(mc);//设置VedioView与MediaController相关联
        videoView.setVideoURI(Uri.parse(getIntent().getStringExtra("url")));
        videoView.start();
    }
}
