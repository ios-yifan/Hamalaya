package com.androidstudy.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetailViewCallback {

    /**
     * 专辑内容
     * @param tracks
     */
    void onDetailListLoaded(List<Track> tracks);

    /**
     * 网络错误
     * @param i
     * @param s
     */
    void onNetworkError(int i, String s);

    /**
     * album 传给 UI
     * @param album
     */
    void onAlbumLoaded(Album album);

    /**
     * 加载更多的结果
     * @param size
     */
    void onLoaderMoreFinished(int size);

    /**
     * 下拉加载更多的结果
     * @param size
     */
    void onRefreshFinished(int size);

}
