package com.androidstudy.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;
import java.util.Queue;

public interface ISearchCallBack {

    /**
     * 搜索结果的回调
     * @param result
     */
    void onSearchResultLoaded(List<Album> result);

    /**
     * 获取推荐热词的结果
     * @param hotWordList
     */
    void onHotWordLoaded(List<HotWord> hotWordList);

    /**
     * 加载更多的结果返回
     * @param result 结果
     * @param isOK  true 成功 false 没有更多
     */
    void onLoadMoreResult(List<Album> result, boolean isOK);

    /**
     * 联想关键字的结果回调
     * @param keyWorkList
     */
    void onRecommendWordLoaded(List<QueryResult> keyWorkList);

    void onError(int errorCode, String errorMsg);

}
