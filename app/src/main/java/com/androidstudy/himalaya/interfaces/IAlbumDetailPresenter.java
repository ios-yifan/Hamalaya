package com.androidstudy.himalaya.interfaces;

import com.androidstudy.himalaya.DetailActivity;

public interface IAlbumDetailPresenter {

    /**
     * 获取专辑详情
     *
     * @param albumId
     * @param page
     */
    void getAlbumDetail(int albumId, int page);

    void pullRefreshMore();

    void loadMore();

    /**
     * 注册 UI通知接口
     * @param detailViewCallback
     */
    void registerViewCallback(IAlbumDetailViewCallback detailViewCallback);

    /**
     * 删除 UI 通知接口
     * @param detailViewCallback
     */
    void unRegisterViewCallback(IAlbumDetailViewCallback detailViewCallback);


}
