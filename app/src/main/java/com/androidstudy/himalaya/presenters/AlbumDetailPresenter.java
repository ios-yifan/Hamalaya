package com.androidstudy.himalaya.presenters;

import android.util.Log;

import com.androidstudy.himalaya.data.XimalayaApi;
import com.androidstudy.himalaya.interfaces.IAlbumDetailPresenter;
import com.androidstudy.himalaya.interfaces.IAlbumDetailViewCallback;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.List;

/**
 * 接口的实现类
 */
public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private static final String TAG = "AlbumDetailPresenter";
    private Album mTargetAlbum = null;
    private List<IAlbumDetailViewCallback> callbacks = new ArrayList<>();
    private Object String;
    private int currentId = -1;
    private int currendPage = 0;
    private List<Track> mTracks = new ArrayList<>();


    private AlbumDetailPresenter() {
    }

    private static AlbumDetailPresenter sInstance = null;


    public static AlbumDetailPresenter getInstance() {
        if (sInstance == null) {
            synchronized (AlbumDetailPresenter.class) {
                sInstance = new AlbumDetailPresenter();
            }
        }
        return sInstance;
    }

    private void doLoaded(final boolean isLoadedMore) {

        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();

        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    Log.d(TAG, "onSuccess: size > " + tracks.size());
                    if (isLoadedMore) {
                        mTracks.addAll(tracks);
                        int size = tracks.size();
                        handlerLoaderMoreResult(size);
                    } else {
                        mTracks.addAll(0, tracks);
                    }

                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int i, java.lang.String s) {
                if (isLoadedMore) {
                    currendPage--;
                }
                handlerError(i, s);
            }
        },currentId,currendPage);

    }

    /**
     * 处理加载更多的结果
     * @param size
     */
    private void handlerLoaderMoreResult(int size) {
        for (IAlbumDetailViewCallback callback : callbacks) {

            callback.onLoaderMoreFinished(size);
        }
    }

    @Override
    public void getAlbumDetail(int albumId, int page) {

        mTracks.clear();
        this.currentId = albumId;
        this.currendPage = page;
        //根据页码和 albumid 获取列表
        doLoaded(false);
    }

    private void handlerError(int i, String s) {

        for (IAlbumDetailViewCallback callback : callbacks) {
            callback.onNetworkError(i, s);
        }
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {

        for (IAlbumDetailViewCallback callback : callbacks) {
            callback.onDetailListLoaded(tracks);
        }
    }

    @Override
    public void pullRefreshMore() {

    }

    @Override
    public void loadMore() {
        //加载更多
        currendPage++;
        doLoaded(true);
    }

    @Override
    public void registerViewCallback(IAlbumDetailViewCallback detailViewCallback) {

        if (!callbacks.contains(detailViewCallback)) {
            callbacks.add(detailViewCallback);
            if (mTargetAlbum != null) {
                detailViewCallback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unRegisterViewCallback(IAlbumDetailViewCallback detailViewCallback) {

        if (callbacks.contains(detailViewCallback)) {
            callbacks.remove(detailViewCallback);
        }
    }


    public void setTargetAlbum(Album targetAlbum) {
        this.mTargetAlbum = targetAlbum;
    }
}
