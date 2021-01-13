package com.androidstudy.himalaya.presenters;

import android.os.Trace;
import android.util.Log;

import com.androidstudy.himalaya.base.BaseApplication;
import com.androidstudy.himalaya.interfaces.IPlayerPresenter;
import com.androidstudy.himalaya.interfaces.IPlayerViewCallback;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private static final String TAG = "PlayerPresenter";
    private static PlayerPresenter sPlayerPresenter;
    private final XmPlayerManager mInstance;
    private List<IPlayerViewCallback> mIPlayerViewCallbacks = new ArrayList<>();

    private PlayerPresenter(){
        mInstance = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        mInstance.addAdsStatusListener(this);
        mInstance.addPlayerStatusListener(this);
    }

    public static PlayerPresenter getPlayerPresenter(){
        if (sPlayerPresenter == null) {
            synchronized (PlayerPresenter.class){
                sPlayerPresenter = new PlayerPresenter();
            }
        }
        return sPlayerPresenter;
    }

    private boolean isPlayListSet = false;
    public void setPlayList(List<Track> list, int playIndex){
        if (list != null) {
            mInstance.setPlayList(list,playIndex);
            isPlayListSet = true;
        }
    }

    @Override
    public void play() {
        if (isPlayListSet){
            mInstance.play();
        }
    }

    @Override
    public void pause() {
        if (isPlayListSet){
            mInstance.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        if (mInstance != null) {
            mInstance.playPre();
        }

    }

    @Override
    public void playNext() {
        if (mInstance != null){
            mInstance.playNext();
        }

    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void getPlayList() {

    }

    @Override
    public void playByIndex(int index) {

    }

    @Override
    public void seekTo(int progress) {
        //更新播放器的进度
        mInstance.seekTo(progress);
    }

    @Override
    public boolean isPlay() {
        // 返回当前是否正在播放
        return mInstance.isPlaying();
    }

    @Override
    public void registerViewCallback(IPlayerViewCallback iPlayerViewCallback) {

        if (!mIPlayerViewCallbacks.contains(iPlayerViewCallback)) {
            mIPlayerViewCallbacks.add(iPlayerViewCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerViewCallback iPlayerViewCallback) {
    mIPlayerViewCallbacks.remove(iPlayerViewCallback);
    }

    /*广告相关回调*/
    @Override
    public void onStartGetAdsInfo() {

        Log.d(TAG, "onStartGetAdsInfo: ");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        Log.d(TAG, "onGetAdsInfo: ");
    }

    @Override
    public void onAdsStartBuffering() {

        Log.d(TAG, "onAdsStartBuffering: ");
    }

    @Override
    public void onAdsStopBuffering() {
        Log.d(TAG, "onAdsStopBuffering: ");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        Log.d(TAG, "onStartPlayAds: ");
    }

    @Override
    public void onCompletePlayAds() {
        Log.d(TAG, "onCompletePlayAds: ");
    }

    /*播放器相关*/
    @Override
    public void onError(int i, int i1) {
        Log.d(TAG, "onError: ");
    }

    @Override
    public void onPlayStart() {

        Log.d(TAG, "onPlayStart: ");
        for (IPlayerViewCallback iPlayerViewCallback : mIPlayerViewCallbacks) {
            iPlayerViewCallback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        Log.d(TAG, "onPlayPause: ");
        for (IPlayerViewCallback iPlayerViewCallback : mIPlayerViewCallbacks) {
            iPlayerViewCallback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        Log.d(TAG, "onPlayStop: ");
        for (IPlayerViewCallback iPlayerViewCallback : mIPlayerViewCallbacks) {
            iPlayerViewCallback.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {
        Log.d(TAG, "onSoundPlayComplete: ");
    }

    @Override
    public void onSoundPrepared() {
        Log.d(TAG, "onSoundPrepared: ");
    }

    @Override
    public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {
        Log.d(TAG, "onSoundSwitch: ");
    }

    @Override
    public void onBufferingStart() {
        Log.d(TAG, "onBufferingStart: ");
    }

    @Override
    public void onBufferingStop() {
        Log.d(TAG, "onBufferingStop: ");
    }

    @Override
    public void onBufferProgress(int i) {
        Log.d(TAG, "onBufferProgress: " + i);
    }

    @Override
    public void onPlayProgress(int i, int i1) {
        Log.d(TAG, "onPlayProgress: " + i + ">>>" + i1);
        for (IPlayerViewCallback iPlayerViewCallback : mIPlayerViewCallbacks) {
            iPlayerViewCallback.onProgressChange(i,i1);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        Log.d(TAG, "onError: " + e.toString());
        return false;
    }
}
