package com.androidstudy.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubscriptionCallback {

    /**
     * 当调用添加的时候，去通知 UI 结果
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 当调用删除的时候，去通知 UI 结果
     * @param isSuccess
     */
    void onDeleteResult(boolean isSuccess);

    /**
     * 获取订阅专辑的加载结果回调
     * @param albumList
     */
    void onSubscriptionsLoaded(List<Album> albumList);

}
