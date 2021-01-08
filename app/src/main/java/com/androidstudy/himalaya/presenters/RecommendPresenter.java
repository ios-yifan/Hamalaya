package com.androidstudy.himalaya.presenters;

import com.androidstudy.himalaya.interfaces.IRecommendPresenter;
import com.androidstudy.himalaya.interfaces.IRecommendViewCallback;
import com.androidstudy.himalaya.utils.Constants;
import com.androidstudy.himalaya.utils.LogUtils;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPresenter implements IRecommendPresenter {

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
     * 获取推荐内容
     */
    public void getRecommendList() {
        updateLoading();
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT + "");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {

                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    if (albumList != null) {
                        handlerRecommendResult(albumList);
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
