package com.androidstudy.himalaya.interfaces;

import android.os.Trace;

import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerViewCallback {

    /**
     * 开始播放
     */
    void onPlayStart();

    /**
     * 暂停播放
     */
    void onPlayPause();

    /**
     * 暂停播放
     */
    void onPlayStop();

    /**
     * 播放错误
     */
    void onPlayError();

    /**
     * 下一首
     */
    void onNextPlay(Trace trace);

    /**
     * 上一首
     */
    void onPrePlay(Trace trace);

    /**
     * 播放列表数据加载完成
     * @param list
     */
    void onListLoaded(List<Trace> list);

    /**
     * 播放模式改变了
     * @param playMode
     */
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);

    /**
     * 进度条的改变
     * @param currentProgress
     * @param total
     */
    void onProgressChange(long currentProgress, long total);

    /**
     * 广告正在加载
     */
    void onAdLoading();

    /**
     * 广告结束
     */
    void onAdFinished();
}

