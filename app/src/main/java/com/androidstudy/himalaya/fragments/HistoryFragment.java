package com.androidstudy.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidstudy.himalaya.PlayerActivity;
import com.androidstudy.himalaya.R;
import com.androidstudy.himalaya.adapters.TrackListAdapter;
import com.androidstudy.himalaya.base.BaseApplication;
import com.androidstudy.himalaya.base.BaseFragment;
import com.androidstudy.himalaya.interfaces.IHistoryCallback;
import com.androidstudy.himalaya.presenters.HistoryPresenter;
import com.androidstudy.himalaya.presenters.PlayerPresenter;
import com.androidstudy.himalaya.views.ConfirmCheckBoxDialog;
import com.androidstudy.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class HistoryFragment extends BaseFragment implements IHistoryCallback, TrackListAdapter.ItemClickListener, TrackListAdapter.ItemLongClickListener, ConfirmCheckBoxDialog.OnDialogActionListener {

    private UILoader mUiLoader;
    private TrackListAdapter mTrackListAdapter;
    private HistoryPresenter mHistoryPresenter;
    private Track mCurrentClickHistoryItem = null;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {

        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragment_history,container,false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(BaseApplication.getAppContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }

                @Override
                protected View getEntryView() {
                    View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_entry_view,this,false);
                    TextView tips = emptyView.findViewById(R.id.empty_tv);
                    tips.setText("没有历史记录呢！");
                    return emptyView;
                }
            };
        } else {
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
        }

        //presenter
        mHistoryPresenter = HistoryPresenter.getInstance();
        mHistoryPresenter.registerViewCallback(this);
        mHistoryPresenter.listHistories();
        mUiLoader.updateStatus(UILoader.UIStatus.LAODING);

        rootView.addView(mUiLoader);

        return rootView;
    }

    private View createSuccessView(ViewGroup container) {

        View inflate = LayoutInflater.from(container.getContext()).inflate(R.layout.item_history, null);
        TwinklingRefreshLayout refreshLayout = inflate.findViewById(R.id.history_scroll_view);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);
        RecyclerView recyclerView = inflate.findViewById(R.id.history_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        //设置item 间距
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),2);
                outRect.bottom = UIUtil.dip2px(view.getContext(),2);
                outRect.left = UIUtil.dip2px(view.getContext(),2);
                outRect.right = UIUtil.dip2px(view.getContext(),2);
            }
        });
        mTrackListAdapter = new TrackListAdapter();
        mTrackListAdapter.setItemClickListener(this);
        mTrackListAdapter.setItemLongClickListener(this);
        recyclerView.setAdapter(mTrackListAdapter);
        return inflate;
    }

    @Override
    public void onHistoryLoaded(List<Track> tracks) {
        if (tracks.size() == 0 || tracks == null) {
            mUiLoader.updateStatus(UILoader.UIStatus.EMTRY);
        } else {
            //更新数据
            mTrackListAdapter.setData(tracks);
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHistoryPresenter != null) {
            mHistoryPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onItemClick(List<Track> list, int position) {
        //设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(list,position);
        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Track track) {
        this.mCurrentClickHistoryItem = track;
        //删除历史
        ConfirmCheckBoxDialog dialog = new ConfirmCheckBoxDialog(getActivity());
        dialog.setOnDialogActionListener(this);
        dialog.show();
    }

    @Override
    public void onCancelSubClick() {

    }

    @Override
    public void onGiveUp(boolean checked) {

        if (mHistoryPresenter != null && mCurrentClickHistoryItem != null) {
            if (!checked) {
                mHistoryPresenter.delHistory(mCurrentClickHistoryItem);
            } else {
                mHistoryPresenter.clearHistories();
            }
        }
    }
}
