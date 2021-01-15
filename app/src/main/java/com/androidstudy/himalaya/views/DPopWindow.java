package com.androidstudy.himalaya.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidstudy.himalaya.R;
import com.androidstudy.himalaya.adapters.PlayListAdapter;
import com.androidstudy.himalaya.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public class DPopWindow extends PopupWindow {

    private final View mPopView;
    private View mCloseBtn;
    private RecyclerView mTarckList;
    private PlayListAdapter mAdapter;
    private PlayListItemClickListener playListItemClickListener;
    private TextView mPlayModeTv;
    private View playModeIv;
    private ImageView mPlayModeIv;
    private View mContainer;
    private PlaylistPlayModeClickListener mPlaylistPlayModeClickListener = null;

    public DPopWindow() {
        //载入 view
        super(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置这个属性之前要先设置setBackgroundDrawable，否则无法关闭
        setOutsideTouchable(true);
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        setContentView(mPopView);

        // 设置窗口进入退出动画

        setAnimationStyle(R.style.pop_animation);
        
        
        
        initView();
        initEvent();
    }

    private void initView() {
        mCloseBtn = mPopView.findViewById(R.id.play_list_close_btn);
        mTarckList = mPopView.findViewById(R.id.play_list_rv);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BaseApplication.getAppContext());
        mTarckList.setLayoutManager(linearLayoutManager);

        mAdapter = new PlayListAdapter();
        mTarckList.setAdapter(mAdapter);

        mPlayModeTv = mPopView.findViewById(R.id.play_list_play_mode_tv);
        mPlayModeIv = mPopView.findViewById(R.id.play_list_play_mode_iv);
        mContainer = mPopView.findViewById(R.id.play_list_play_mode_container);

    }

    private void initEvent() {
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击窗口消失
                DPopWindow.this.dismiss();
            }
        });

        mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlaylistPlayModeClickListener != null) {
                    mPlaylistPlayModeClickListener.onPlayModeClick();
                }
            }
        });

    }

    /**
     * 给适配器设置数据
     * @param data
     */
    public void setListData(List<Track> data){

        if (mAdapter != null) {
            mAdapter.setData(data);
        }
    }

    public void setCurrentPlayPosition(int playPosition){
        if (mAdapter != null) {
            mAdapter.setCurrentPlayPosition(playPosition);
            mTarckList.scrollToPosition(playPosition);
        }
    }

    public void setPlayListItemClickListener(PlayListItemClickListener listener){
        mAdapter.setOnClickItemListener(listener);
    }
    public interface PlayListItemClickListener{
        void onClickItem(int position);
    }

    public void setPlaylistPlayModeClickListener(PlaylistPlayModeClickListener listener){
        mPlaylistPlayModeClickListener = listener;
    }

    public interface PlaylistPlayModeClickListener{
        void onPlayModeClick();
    }
}
