package com.androidstudy.himalaya.interfaces;

import com.androidstudy.himalaya.base.IBasePresenter;

public interface ISearchPresenter extends IBasePresenter<ISearchCallBack> {

    /**
     * 进行搜索
     * @param keyword
     */
    void doSearch(String keyword);

    /**
     * 重新搜索
     */
    void reSearch();

    /**
     * 加载更多的搜索结果
     */
    void loaderMode();

    /**
     * 获取热词
     */
    void getHotWord();

    /**
     * 获取推荐的关键词
     * @param keyword
     */
    void getRecommendWork(String keyword);
}
