package com.androidstudy.himalaya.base;

import com.androidstudy.himalaya.interfaces.IAlbumDetailViewCallback;

public interface IBasePresenter<T> {


    /**
     * 注册 UI 回调接口
     * @param
     */
    void registerViewCallback(T t);

    /**
     * 取消注册 UI 的回调接口
     * @param
     */
    void unRegisterViewCallback(T t);
}
