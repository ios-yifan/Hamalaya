package com.androidstudy.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubDaoCallback {

    /**
     * 添加的结果回调
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 删除结果回调
     * @param isSuccess
     */
    void onDelResult(boolean isSuccess);

    /**
     * 获取订阅内容
     * @param result
     */
    void onSubListLoaded(List<Album> result);


}
