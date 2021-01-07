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
     * album 传给 UI
     * @param album
     */
    void onAlbumLoaded(Album album);

}
