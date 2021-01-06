package com.androidstudy.himalaya.fragments;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidstudy.himalaya.R;
import com.androidstudy.himalaya.adapters.RecommendListAdapter;
import com.androidstudy.himalaya.base.BaseFragment;
import com.androidstudy.himalaya.utils.Constants;
import com.androidstudy.himalaya.utils.LogUtils;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment {

    private final String TAG = "RecommendFragment";
    private View rootView;
    private RecyclerView mRecommendRv;
    private RecommendListAdapter recommendListAdapter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        rootView = layoutInflater.inflate(R.layout.fragment_recomment,container,false);

        mRecommendRv = rootView.findViewById(R.id.recommend_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);
        mRecommendRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });

        recommendListAdapter = new RecommendListAdapter();
        mRecommendRv.setAdapter(recommendListAdapter);


        //获取数据
        getRecommendData();

        return rootView;
    }

    /**
     * 获取推荐内容
     */
    private void getRecommendData() {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT + "");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {

                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    if (albumList != null) {
                        LogUtils.d(TAG,"size >> " + albumList.size());
                        upRecommendUI(albumList);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

                LogUtils.d(TAG,s);

            }
        });
    }

    private void upRecommendUI(List<Album> albumList) {

        recommendListAdapter.setData(albumList);

    }
}
