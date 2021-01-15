package com.androidstudy.himalaya.api;

import com.androidstudy.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.HashMap;
import java.util.Map;

public class XimalayaApi {

    private XimalayaApi(){

    }

    private static XimalayaApi sXimalayaApi;

    public static XimalayaApi getXimalayaApi(){
        if (sXimalayaApi == null){
            synchronized (XimalayaApi.class){
                sXimalayaApi = new XimalayaApi();
            }
        }
        return sXimalayaApi;
    }


    /**
     * 获取推荐内容
     * @param callBack
     */
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callBack){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT + "");
        CommonRequest.getGuessLikeAlbum(map,callBack);
    }

    /**
     * 根据专辑 ID 获取专辑内容
     * @param callBack
     * @param albumId
     * @param pageIndex
     */
    public void getAlbumDetail(IDataCallBack<TrackList> callBack,long albumId, int pageIndex){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, pageIndex + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getTracks(map, callBack);
    }
}
