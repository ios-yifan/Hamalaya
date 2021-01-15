package com.androidstudy.himalaya.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

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
    private PlaylistActionListener mPlaylistPlayModeClickListener = null;
    private View mOrderContainer;
    private ImageView mOrderIcon;
    private TextView mOrderTv;

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

        mOrderContainer = mPopView.findViewById(R.id.play_list_order_container);
        mOrderIcon = mPopView.findViewById(R.id.play_list_play_order_iv);
        mOrderTv = mPopView.findViewById(R.id.play_list_play_order_tv);
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

        mOrderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlaylistPlayModeClickListener != null) {
                    mPlaylistPlayModeClickListener.onOrderClick();
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

    public void updatePlayMode(XmPlayListControl.PlayMode currentMode) {
        updatePlayModeBtnImage(currentMode);
    }

    public void updateOrderIcon(boolean isOrder){
        mOrderIcon.setImageResource(isOrder ? R.drawable.selector_player_mode_list_revers : R.drawable.selector_player_mode_list_order);
        mOrderTv.setText(isOrder? "逆序" :"顺序");
    }

    /**
     * 根据当前的状态更新播放模式图标
     */
    private void updatePlayModeBtnImage(XmPlayListControl.PlayMode playMode) {
        int textId = R.string.play_mode_order_text;
        switch (playMode){
            case PLAY_MODEL_LIST:
                mPlayModeIv.setImageResource(R.drawable.selector_player_mode_list_order);
                textId = R.string.play_mode_order_text;
                break;
            case PLAY_MODEL_LIST_LOOP:
                mPlayModeIv.setImageResource(R.drawable.selector_player_mode_list_loop);
                textId = R.string.play_mode_list_play_text;
                break;
            case PLAY_MODEL_RANDOM:
                mPlayModeIv.setImageResource(R.drawable.selector_player_mode_random);
                textId = R.string.play_mode_random_text;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                mPlayModeIv.setImageResource(R.drawable.selector_player_mode_single_loop);
                textId = R.string.play_mode_single_play_text;
                break;
        }

        mPlayModeTv.setText(textId);
    }


    public interface PlayListItemClickListener{
        void onClickItem(int position);
    }

    public void setPlaylistPlayModeClickListener(PlaylistActionListener listener){
        mPlaylistPlayModeClickListener = listener;
    }

    public interface PlaylistActionListener {

        /**
         * 播放模式被点击
         */
        void onPlayModeClick();

        /**
         * 播放顺序点击
         */
        void onOrderClick();
    }


}
