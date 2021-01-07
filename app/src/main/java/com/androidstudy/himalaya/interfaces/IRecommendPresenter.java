package com.androidstudy.himalaya.interfaces;

public interface IRecommendPresenter {

    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 下拉刷新更多内容
     */
    void pullRefreshMore();

    /**
     * 上拉加载
     */
    void loadMore();

    /**
     * 这个方法用于注册 UI 回调
     *
     * @param callback
     */
    void registerViewCallback(IRecommendViewCallback callback);

    /**
     * 取消 UI 注册
     *
     * @param callback
     */
    void unRegisterViewCallback(IRecommendViewCallback callback);

}
