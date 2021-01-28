package com.androidstudy.himalaya.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.androidstudy.himalaya.data.XimalayaApi;
import com.androidstudy.himalaya.base.BaseApplication;
import com.androidstudy.himalaya.interfaces.IPlayerPresenter;
import com.androidstudy.himalaya.interfaces.IPlayerViewCallback;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private static final String TAG = "PlayerPresenter";
    private static PlayerPresenter sPlayerPresenter;
    private final XmPlayerManager mInstance;
    private List<IPlayerViewCallback> mIPlayerViewCallbacks = new ArrayList<>();
    private Track mTrack;
    private int mCurrentIndex = 0;
    private final SharedPreferences mSp;
    private boolean mIsReverse = false;

    private XmPlayListControl.PlayMode mCurrentMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
    /**
     *          PLAY_MODEL_SINGLE,
     *         PLAY_MODEL_SINGLE_LOOP,
     *         PLAY_MODEL_LIST,
     *         PLAY_MODEL_LIST_LOOP,
     *         PLAY_MODEL_RANDOM;
     */
    public static final int  PLAY_MODEL_LIST_INT = 0;
    public static final int PLAY_MODEL_LIST_LOOP_INT = 1;
    public static final int PLAY_MODEL_RANDOM_INT = 2;
    public static final int PLAY_MODEL_SINGLE_LOOP_INT = 3;

    // sp - key name
    public static final String PLAY_MODE_SP_NAME = "PlayMode";
    public static final String PLAY_MODE_SP_KEY = "currentPlayMode";
    private int mCurrentProPosition = 0;
    private int mCurrentProTotal = 0;

    private PlayerPresenter() {
        mInstance = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        mInstance.addAdsStatusListener(this);
        mInstance.addPlayerStatusListener(this);
        //记录当前的播放模式
        mSp = BaseApplication.getAppContext().getSharedPreferences(PLAY_MODE_SP_NAME, Context.MODE_PRIVATE);

    }

    public static PlayerPresenter getPlayerPresenter() {
        if (sPlayerPresenter == null) {
            synchronized (PlayerPresenter.class) {
                sPlayerPresenter = new PlayerPresenter();
            }
        }
        return sPlayerPresenter;
    }

    private boolean isPlayListSet = false;

    public void setPlayList(List<Track> list, int playIndex) {
        if (list != null) {
            mInstance.setPlayList(list, playIndex);
            isPlayListSet = true;
            Track track = list.get(playIndex);
            mTrack = track;
            mCurrentIndex = playIndex;
        }
    }

    @Override
    public void play() {
        if (isPlayListSet) {
            mInstance.play();
        }
    }

    @Override
    public void pause() {
        if (isPlayListSet) {
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
        if (mInstance != null) {
            mInstance.playNext();
        }

    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (mInstance != null) {
            mCurrentMode = mode;
            mInstance.setPlayMode(mode);
            //通知 UI 更新播放模式
            for (IPlayerViewCallback iPlayerViewCallback : mIPlayerViewCallbacks) {
                iPlayerViewCallback.onPlayModeChange(mode);
            }
            //保存到 Sp 中
            SharedPreferences.Editor editor = mSp.edit();
            editor.putInt(PLAY_MODE_SP_KEY,getIntByPlayMode(mode));
            editor.commit();

        }
    }

    private int getIntByPlayMode(XmPlayListControl.PlayMode mode){
        switch (mode){
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT;
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP_INT;
        }
        return PLAY_MODEL_LIST_INT;
    }

    private XmPlayListControl.PlayMode getModeByInt(int index){
        switch (index){
            case PLAY_MODEL_LIST_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
            case PLAY_MODEL_LIST_LOOP_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_RANDOM_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
            case PLAY_MODEL_SINGLE_LOOP_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;
        }
        return XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
    }

    /**
     * 判断是否有播放列表
     * @return
     */
    public boolean hasPlayList(){
        return isPlayListSet;
    }

    @Override
    public void getPlayList() {
        if (mInstance != null) {
            List<Track> playList = mInstance.getPlayList();
            for (IPlayerViewCallback iPlayerViewCallback : mIPlayerViewCallbacks) {
                iPlayerViewCallback.onListLoaded(playList);
            }
        }
    }

    @Override
    public void playByIndex(int index) {
        mInstance.play(index);
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
    public void reversePlayList() {
        //反转播放列表

        List<Track> playList = mInstance.getPlayList();
        Collections.reverse(playList);
        mIsReverse = !mIsReverse;
        mCurrentIndex =  ((playList.size() - 1) - mCurrentIndex);

        mInstance.setPlayList(playList,mCurrentIndex);

        // 更新 UI
        mTrack = (Track) mInstance.getCurrSound();

        for (IPlayerViewCallback iPlayerViewCallback : mIPlayerViewCallbacks) {
            iPlayerViewCallback.onListLoaded(playList);
            iPlayerViewCallback.onTrackUpdate(mTrack,mCurrentIndex);
            iPlayerViewCallback.updateListOrder(mIsReverse);
        }
    }

    @Override
    public void playByAlbumId(long id) {
        //获取到专辑内容
        //设置到播放器
        //开始播放

        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                List<Track> tracks = trackList.getTracks();
                if (tracks != null && tracks.size() > 0) {
                    mInstance.setPlayList(tracks,0);
                    isPlayListSet = true;
                    Track track = tracks.get(0);
                    mTrack = track;
                    mCurrentIndex = 0;
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        },id,1);

    }

    @Override
    public void registerViewCallback(IPlayerViewCallback iPlayerViewCallback) {
        if (!mIPlayerViewCallbacks.contains(iPlayerViewCallback)) {
            mIPlayerViewCallbacks.add(iPlayerViewCallback);
        }
        // 更新之前保证有数据。
        getPlayList();
        iPlayerViewCallback.onProgressChange(mCurrentProPosition,mCurrentProTotal);
        //通知当前节目
        iPlayerViewCallback.onTrackUpdate(mTrack,mCurrentIndex);
        //更新状态
        handlerPlayStatus(iPlayerViewCallback);
        //从 sp 里拿
        int anInt = mSp.getInt(PLAY_MODE_SP_KEY, PLAY_MODEL_LIST_INT);
        mCurrentMode = getModeByInt(anInt);
        iPlayerViewCallback.onPlayModeChange(mCurrentMode);

    }

    private void handlerPlayStatus(IPlayerViewCallback iPlayerViewCallback) {
        int playerStatus = mInstance.getPlayerStatus();
        //根据状态调用
        if (PlayerConstants.STATE_STARTED == playerStatus) {
            iPlayerViewCallback.onPlayStart();
        } else {
            iPlayerViewCallback.onPlayPause();
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
        mInstance.setPlayMode(mCurrentMode);
        Log.d(TAG, "onSoundPrepared: ");
        if (mInstance.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            mInstance.play();
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {
        Log.d(TAG, "onSoundSwitch: ");
        // currentModel 当前播放的内容
        // 通过 getKind 获取是什么类型
        // track 表示 track 类型
        // 第一种写法 不推荐
        //if ("track".equals(playableModel.getKind())) {
        //    Track curTrack = (Track)playableModel;
        //    Log.d(TAG, "onSoundSwitch: playableModel.title" + curTrack.getTrackTitle());
        //}
        mCurrentIndex = mInstance.getCurrentIndex();

        if (playableModel != null) {

        }
        // 第二种写法
        if (playableModel1 instanceof Track) {
            Track curTrack = (Track) playableModel1;
            Log.d(TAG, "onSoundSwitch: playableModel.title" + curTrack.getTrackTitle());
            mTrack = curTrack;
            //报错播放记录
            HistoryPresenter historyPresenter = HistoryPresenter.getInstance();
            historyPresenter.addHistory(curTrack);
            for (IPlayerViewCallback iPlayerViewCallback : mIPlayerViewCallbacks) {
                iPlayerViewCallback.onTrackUpdate(mTrack,mCurrentIndex);
            }
        }

        // 修改页面图片
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
        this.mCurrentProPosition = i;
        this.mCurrentProTotal = i1;
        for (IPlayerViewCallback iPlayerViewCallback : mIPlayerViewCallbacks) {
            iPlayerViewCallback.onProgressChange(i, i1);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        Log.d(TAG, "onError: " + e.toString());
        return false;
    }
}
