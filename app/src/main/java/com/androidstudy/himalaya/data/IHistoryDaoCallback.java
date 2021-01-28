package com.androidstudy.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryDaoCallback {

    /**
     * 添加历史的结果
     * @param isSuccess
     */
    void onHistoryAdd(boolean isSuccess);

    /**
     * 删除历史的结果
     * @param isSuccess
     */
    void onHistoryDel(boolean isSuccess);

    /**
     * 加载历史的结果
     * @param tracks
     */
    void onHistoriesLoaded(List<Track> tracks);

    /**
     * 清除历史消息的结果
     */
    void onHistoriesClear();
}
