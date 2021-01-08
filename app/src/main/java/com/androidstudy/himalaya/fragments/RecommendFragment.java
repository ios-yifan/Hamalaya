package com.androidstudy.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidstudy.himalaya.DetailActivity;
import com.androidstudy.himalaya.R;
import com.androidstudy.himalaya.adapters.RecommendListAdapter;
import com.androidstudy.himalaya.base.BaseFragment;
import com.androidstudy.himalaya.interfaces.IRecommendViewCallback;
import com.androidstudy.himalaya.presenters.AlbumDetailPresenter;
import com.androidstudy.himalaya.presenters.RecommendPresenter;
import com.androidstudy.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback, UILoader.OnRetryClickListener, RecommendListAdapter.onRecommendItemClickListener {

    private final String TAG = "RecommendFragment";
    private View rootView;
    private RecyclerView mRecommendRv;
    private RecommendListAdapter recommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader mLoader;

    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, final ViewGroup container) {
        mLoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater,container);
            }
        };

        //获取到逻辑对象
        mRecommendPresenter = RecommendPresenter.getInstance();

        //先设置通知接口
        mRecommendPresenter.registerViewCallback(this);
        mLoader.setOnRetryClickListener(this);

        mRecommendPresenter.getRecommendList();

        if (mLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mLoader.getParent()).removeView(mLoader);
        }
        return mLoader;
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
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
        recommendListAdapter.setonRecommendItemClickListener(this);
        return rootView;
    }


    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //获取到推荐内容，这个方法就会触发
        //数据回来更新 UI
        recommendListAdapter.setData(result);
        mLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        mLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        mLoader.updateStatus(UILoader.UIStatus.EMTRY);
    }

    @Override
    public void onLoading() {
        mLoader.updateStatus(UILoader.UIStatus.LAODING);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口注册，防止内存泄露
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        // 网络不佳的时候 点击重试
        if (mRecommendPresenter != null) {
            mRecommendPresenter.getRecommendList();
        }
    }

    @Override
    public void onItemClick(int position, Album data) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(data);
        //点击跳转到详情界面
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }
}
