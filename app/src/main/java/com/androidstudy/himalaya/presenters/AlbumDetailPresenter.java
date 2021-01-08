package com.androidstudy.himalaya.presenters;

import android.util.Log;

import com.androidstudy.himalaya.interfaces.IAlbumDetailPresenter;
import com.androidstudy.himalaya.interfaces.IAlbumDetailViewCallback;
import com.androidstudy.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口的实现类
 */
public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private static final String TAG = "AlbumDetailPresenter";
    private Album mTargetAlbum = null;
    private List<IAlbumDetailViewCallback> callbacks = new ArrayList<>();


    private AlbumDetailPresenter() {
    }

    private static AlbumDetailPresenter sInstance = null;


    public static AlbumDetailPresenter getInstance(){
        if (sInstance == null) {
            synchronized (AlbumDetailPresenter.class){
                sInstance = new AlbumDetailPresenter();
            }
        }
        return sInstance;
    }

    @Override
    public void getAlbumDetail(int albumId, int page) {

        //根据页码和 albumid 获取列表
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID,albumId+"");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT+"");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {

                if (trackList != null) {

                    Log.d(TAG, "onSuccess: thread >>" + Thread.currentThread().getName());
                    List<Track> tracks = trackList.getTracks();

                    Log.d(TAG, "onSuccess: size > " + tracks.size());
                    handlerAlbumDetailResult(tracks);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
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


    public void setTargetAlbum(Album targetAlbum){
        this.mTargetAlbum = targetAlbum;
    }
}
