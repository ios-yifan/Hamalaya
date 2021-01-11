package com.androidstudy.himalaya;

import android.os.Bundle;
import android.os.Trace;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.androidstudy.himalaya.base.BaseActivity;
import com.androidstudy.himalaya.interfaces.IPlayerViewCallback;
import com.androidstudy.himalaya.presenters.PlayerPresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public class PlayerActivity extends BaseActivity implements IPlayerViewCallback {

    private static final String TAG = "PlayerActivity";
    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);

        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        initView();
        initEvent();
        startPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 释放数据源 
         */
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter = null;
        }
    }

    /**
     * 开始播放
     */
    private void startPlay() {
        if (mPlayerPresenter != null) {
            mPlayerPresenter.play();
        }
    }

    /**
     * 给控件添加事件
     */
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter.isPlay()){
                    mPlayerPresenter.pause();
                }else{
                    mPlayerPresenter.play();
                }
            }
        });
    }

    private void initView() {

        mControlBtn = findViewById(R.id.play_or_pause_btn);
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onPlayStart() {
        Log.d(TAG,  "onPlayStart: 开始播放");
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.stop_normal);
        }
    }

    @Override
    public void onPlayPause() {
        Log.d(TAG,  "onPlayPause: 播放暂停");
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.play_black_normal);
        }
    }

    @Override
    public void onPlayStop() {
        Log.d(TAG,  "onPlayStop: 播放停止");
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.play_black_normal);
        }
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onNextPlay(Trace trace) {

    }

    @Override
    public void onPrePlay(Trace trace) {

    }

    @Override
    public void onListLoaded(List<Trace> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(long currentProgress, long total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }
}
