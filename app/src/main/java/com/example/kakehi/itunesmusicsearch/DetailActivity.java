package com.example.kakehi.itunesmusicsearch;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // ActionBarのタイトルを設定する
        String trackName = getIntent().getExtras().getString("track_name");
        getSupportActionBar().setTitle(trackName);

        String previewUrl = getIntent().getExtras().getString("preview_url");
        if (!TextUtils.isEmpty(previewUrl)) {
            VideoView videoView = (VideoView) findViewById(R.id.video_view);
            videoView.setMediaController(new MediaController(this)); // 再生ボタンとかをつける
            videoView.setVideoURI(Uri.parse(previewUrl)); // URLを設定する
            videoView.start(); // 再生する
        }
    }
}
