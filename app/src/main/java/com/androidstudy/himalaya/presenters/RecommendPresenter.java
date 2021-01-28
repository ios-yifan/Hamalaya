package com.androidstudy.himalaya.presenters;

import android.util.Log;

import com.androidstudy.himalaya.data.XimalayaApi;
import com.androidstudy.himalaya.interfaces.IRecommendPresenter;
import com.androidstudy.himalaya.interfaces.IRecommendViewCallback;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

public class RecommendPresenter implements IRecommendPresenter {

    private static final String TAG = "RecommendPresenter";

    private List<Album> mCurrentRecommend = null;
    private List<Album> mAlbumList;

    public RecommendPresenter() {
    }

    private static RecommendPresenter sInstance = null;

    //存储实现了 callback 的集合
    private List<IRecommendViewCallback> mCallbacks = new ArrayList<>();

    /**
     * 获取单例对象
     *
     * @return
     */
    public static RecommendPresenter getInstance(){
        if (sInstance == null) {
            synchronized (RecommendPresenter.class){
                sInstance = new RecommendPresenter();
            }
        }
        return sInstance;
    }

    /**
     * 获取当前推荐专辑
     * @return
     */
    public List<Album> getCurrentRecommend(){
        return mCurrentRecommend;
    }

    /**
     * 获取推荐内容
     */
    public void getRecommendList() {
        if (mAlbumList != null && mAlbumList.size() > 0) {
            Log.d(TAG, "getRecommendList: memory");
            handlerRecommendResult(mAlbumList);
            return;
        }
        updateLoading();

        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                if (gussLikeAlbumList != null) {
                    mAlbumList = gussLikeAlbumList.getAlbumList();
                    if (mAlbumList != null) {
                        handlerRecommendResult(mAlbumList);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                handlerError();
            }
        });
    }

    private void handlerError() {
        // 通知 UI 更新
        if (mCallbacks != null) {
            for (IRecommendViewCallback callback : mCallbacks) {
                callback.onNetworkError();
            }
        }
    }


    private void handlerRecommendResult(List<Album> albumList) {
        if (albumList != null) {
            if (albumList.size() == 0) {
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onEmpty();
                }
            } else {
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onRecommendListLoaded(albumList);
                }
                this.mCurrentRecommend = albumList;
            }
        }
        // 通知 UI 更新
        if (mCallbacks != null) {
            for (IRecommendViewCallback callback : mCallbacks) {
                callback.onRecommendListLoaded(albumList);
            }
        }
    }

    private void updateLoading(){
        for (IRecommendViewCallback callback : mCallbacks) {
            callback.onLoading();
        }
    }

    /**
     * 外部实现接口注册的时候 将注册接口的对象存储起来。
     */
    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {

        if (mCallbacks != null && !mCallbacks.contains(callback)){
            mCallbacks.add(callback);
        }
    }

    /**
     * 将存起来的对象在取消注册的时候删除掉
     * @param callback
     */
    @Override
    public void unRegisterViewCallback(IRecommendViewCallback callback) {

        if (mCallbacks != null) {
            mCallbacks.remove(mCallbacks);
        }
    }
}
