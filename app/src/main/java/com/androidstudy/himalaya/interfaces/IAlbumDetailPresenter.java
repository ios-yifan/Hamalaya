package com.androidstudy.himalaya.interfaces;

import com.androidstudy.himalaya.DetailActivity;
import com.androidstudy.himalaya.base.IBasePresenter;

public interface IAlbumDetailPresenter extends IBasePresenter<IAlbumDetailViewCallback> {

    /**
     * 获取专辑详情
     *
     * @param albumId
     * @param page
     */
    void getAlbumDetail(int albumId, int page);

    void pullRefreshMore();

    void loadMore();
}
