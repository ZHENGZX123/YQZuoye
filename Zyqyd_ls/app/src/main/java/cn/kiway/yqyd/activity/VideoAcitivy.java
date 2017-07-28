package cn.kiway.yqyd.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.MediaController;
import android.widget.VideoView;

import cn.kiway.yqyd.R;

/**
 * Created by Administrator on 2017/5/26.
 */

public class VideoAcitivy extends Activity {
    VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(Uri.parse(getIntent().getStringExtra("url")));
        videoView.start();
    }
}
