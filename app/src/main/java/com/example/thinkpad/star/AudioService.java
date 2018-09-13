package com.example.thinkpad.star;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;

import java.io.IOException;

public class AudioService extends Service {
    private MediaPlayer mp;
    private String query;
    @Override
    public void onCreate() {
        System.out.println("初始化音乐资源  ");
        super.onCreate();
    }

    public void onStart(Intent intent) {
        if (query != null && !query.equals(intent.getStringExtra("query")) && mp != null) {
            mp.start();
        }
        else{
            //AudioManager am = mContext.getSystemService(Context.AUDIO_SERVICE);

            mp = new MediaPlayer();
            //String url = intent.getStringExtra("query");
            //Uri location = Uri.parse(url);
            try {
                mp.setDataSource("/main/res/raw/ttsapi.mp3");

            } catch (IOException e) {
                e.printStackTrace();
            }
            //mp = MediaPlayer.create(this,R.raw.ttsapi);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        // 不循环播放
                        try {
                            // mp.start();
                            System.out.println("stopped");
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // 播放音乐时发生错误的事件处理
                mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        // 释放资源
                        try {
                            mp.release();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });
            }
    }

    public void onDestroy() {
        // 服务停止时停止播放音乐并释放资源
        mp.stop();
        mp.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
