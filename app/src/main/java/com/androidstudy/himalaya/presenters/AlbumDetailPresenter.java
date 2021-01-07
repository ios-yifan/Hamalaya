package com.androidstudy.himalaya.presenters;

import com.androidstudy.himalaya.interfaces.IAlbumDetailPresenter;
import com.androidstudy.himalaya.interfaces.IAlbumDetailViewCallback;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetailPresenter implements IAlbumDetailPresenter {

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
