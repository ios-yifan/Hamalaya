package com.androidstudy.himalaya.fragments;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidstudy.himalaya.R;
import com.androidstudy.himalaya.adapters.AlbumListAdapter;
import com.androidstudy.himalaya.base.BaseFragment;
import com.androidstudy.himalaya.interfaces.ISubscriptionCallback;
import com.androidstudy.himalaya.interfaces.ISubscriptionPresenter;
import com.androidstudy.himalaya.presenters.SubscriptionPresenter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class SubscriptionFragment extends BaseFragment implements ISubscriptionCallback {

    private SubscriptionPresenter mSubscriptionPresenter;
    private RecyclerView mSubListView;
    private AlbumListAdapter mAlbumListAdapter;
    private TwinklingRefreshLayout mTwinklingRefreshLayout;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView = layoutInflater.inflate(R.layout.fragment_subscription,container,false);

        mTwinklingRefreshLayout = rootView.findViewById(R.id.subscription_scroll_view);
        mTwinklingRefreshLayout.setEnableRefresh(false);
        mTwinklingRefreshLayout.setEnableLoadmore(false);
        mSubListView = rootView.findViewById(R.id.subscription_list);
        mSubListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mAlbumListAdapter = new AlbumListAdapter();
        mSubListView.setAdapter(mAlbumListAdapter);
        mSubListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });

        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);
        mSubscriptionPresenter.getSubscriptionList();
        return rootView;
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {

    }

    @Override
    public void onSubscriptionsLoaded(List<Album> albumList) {

        //更新 UI
        if (mAlbumListAdapter != null) {
            mAlbumListAdapter.setData(albumList);
        }
    }
}
