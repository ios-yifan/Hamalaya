package com.androidstudy.himalaya;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.androidstudy.himalaya.adapters.PlayerTrackPageAdapter;
import com.androidstudy.himalaya.base.BaseActivity;
import com.androidstudy.himalaya.interfaces.IPlayerViewCallback;
import com.androidstudy.himalaya.presenters.PlayerPresenter;
import com.androidstudy.himalaya.views.DPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerActivity extends BaseActivity implements IPlayerViewCallback {

    private static final String TAG = "PlayerActivity";
    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;
    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm:ss");
    private TextView mTotalDuration;
    private TextView mCurrentPosition;
    private SeekBar mSeekbar;
    private int currentProgress = 0;
    private boolean mIsUserTouch = false;
    private ImageView mPlayNextBtn;
    private ImageView mPlayPreBtn;
    private TextView mTitle;
    private String mTrackTitleText;
    private PlayerTrackPageAdapter mTrackPageAdapter;
    private ViewPager mTrackPageView;
    private boolean mIsUserSlidePage = false;
    private ImageView mPlayerSwichBtn;
    private static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> sPlayModeRule = new HashMap<>();
    private XmPlayListControl.PlayMode mCurrentMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;

    static {
        sPlayModeRule.put(XmPlayListControl.PlayMode.PLAY_MODEL_LIST, XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
        sPlayModeRule.put(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP, XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM);
        sPlayModeRule.put(XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM, XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP);
        sPlayModeRule.put(XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP, XmPlayListControl.PlayMode.PLAY_MODEL_LIST);
    }

    private View mPlayListBtn;
    private DPopWindow mDPopWindow;
    private ValueAnimator mEnterBgAnimator;
    private ValueAnimator mOutBgAnimator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);

        initView();
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        mPlayerPresenter.getPlayList();
        initEvent();
        initBgAnimation();

    }

    private void initBgAnimation() {

        mEnterBgAnimator = ValueAnimator.ofFloat(1.0f,0.8f);
        mEnterBgAnimator.setDuration(100);
        mEnterBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                Log.d(TAG, "onAnimationUpdate: " + animation.getAnimatedValue());
                float animatedValue = (float) animation.getAnimatedValue();
                // 处理背景
                updateBgAlpha(animatedValue);
            }
        });


        mOutBgAnimator = ValueAnimator.ofFloat(0.8f,1.0f);
        mOutBgAnimator.setDuration(100);
        mOutBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                // 处理背景
                updateBgAlpha(animatedValue);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 释放数据源 
         */
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter = null;
        }
    }



    /**
     * 给控件添加事件
     */
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter.isPlay()) {
                    mPlayerPresenter.pause();
                } else {
                    mPlayerPresenter.play();
                }
            }
        });

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    currentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //手离开进度条
                mIsUserTouch = false;
                mPlayerPresenter.seekTo(currentProgress);
            }
        });

        //播放下一首
        mPlayNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playNext();
                }
            }
        });

        //播放上一首
        mPlayPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playPre();
                }
            }
        });

        mTrackPageView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mPlayerPresenter != null && mIsUserSlidePage) {
                    //当页面选中，切换播放内容
                    mPlayerPresenter.playByIndex(position);
                }
                mIsUserSlidePage = false;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTrackPageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mIsUserSlidePage = true;
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return false;
            }
        });

        mPlayerSwichBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPlayMode();


            }
        });

        mPlayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDPopWindow.showAtLocation(v, Gravity.BOTTOM,0,0);

                mEnterBgAnimator.start();

            }
        });

        mDPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //窗体消失，恢复透明度
                mOutBgAnimator.start();
            }
        });

        mDPopWindow.setPlayListItemClickListener(new DPopWindow.PlayListItemClickListener() {
            @Override
            public void onClickItem(int position) {
                // 播放列表被点击
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(position);
                }
            }
        });

        mDPopWindow.setPlaylistPlayModeClickListener(new DPopWindow.PlaylistPlayModeClickListener() {
            @Override
            public void onPlayModeClick() {
                switchPlayMode();
            }
        });

    }

    private void switchPlayMode() {
        //根据当前的mode 获取到下一个 Mode
        XmPlayListControl.PlayMode playMode = sPlayModeRule.get(mCurrentMode);
        //修改播放模式
        if (mPlayerPresenter != null) {
            mPlayerPresenter.switchPlayMode(playMode);
        }
    }

    public void updateBgAlpha(float alpha) {
        Log.d(TAG, "updateBgAlpha: >>>>>>>" + alpha);
        Window window = this.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }


    /**
     * 根据当前的状态更新播放模式图标
     */
    private void updatePlayModeBtnImage() {
        switch (mCurrentMode){
            case PLAY_MODEL_LIST:
                mPlayerSwichBtn.setImageResource(R.drawable.selector_player_mode_list_order);
                break;
            case PLAY_MODEL_LIST_LOOP:
                mPlayerSwichBtn.setImageResource(R.drawable.selector_player_mode_list_loop);
                break;
            case PLAY_MODEL_RANDOM:
                mPlayerSwichBtn.setImageResource(R.drawable.selector_player_mode_random);
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                mPlayerSwichBtn.setImageResource(R.drawable.selector_player_mode_single_loop);
                break;
        }
    }



    private void initView() {

        mControlBtn = findViewById(R.id.play_or_pause_btn);
        mTotalDuration = findViewById(R.id.track_duration);
        mCurrentPosition = findViewById(R.id.current_position);
        mSeekbar = findViewById(R.id.track_seek_bar);
        mPlayNextBtn = findViewById(R.id.play_next);
        mPlayPreBtn = findViewById(R.id.play_pre);
        mTitle = findViewById(R.id.track_title);
        if (!TextUtils.isEmpty(mTrackTitleText)) {
            mTitle.setText(mTrackTitleText);
        }
        mTrackPageView = findViewById(R.id.vp_track);
        //设置适配器
        mTrackPageAdapter = new PlayerTrackPageAdapter();
        mTrackPageView.setAdapter(mTrackPageAdapter);
        mPlayerSwichBtn = findViewById(R.id.player_mode_switch_btn);
        mPlayListBtn = findViewById(R.id.player_list);
        mDPopWindow = new DPopWindow();
    }

    @Override
    public void onPlayStart() {
        Log.d(TAG, "onPlayStart: 开始播放");
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_stop);
        }
    }

    @Override
    public void onPlayPause() {
        Log.d(TAG, "onPlayPause: 播放暂停");
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayStop() {
        Log.d(TAG, "onPlayStop: 播放停止");
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onNextPlay(Track trace) {

    }

    @Override
    public void onPrePlay(Track trace) {

    }

    @Override
    public void onListLoaded(List<Track> list) {
        // 把数据设置到适配器里

        if (mTrackPageAdapter != null) {
            mTrackPageAdapter.setData(list);
        }

        if (mDPopWindow != null) {
            //：给播放列表一份
            mDPopWindow.setListData(list);
        }

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {
        mCurrentMode = playMode;
        // 跟新 pop 里的播放模式
        mDPopWindow.updatePlayMode(mCurrentMode);
        updatePlayModeBtnImage();
    }

    @Override
    public void onProgressChange(long currentProgress, long total) {

        mSeekbar.setMax((int) total);
        //更新进度条
        String totalDuration;
        String currentPosition;
        if (total > 1000 * 60 * 60) {
            totalDuration = mHourFormat.format(total);
            currentPosition = mHourFormat.format(currentProgress);
        } else {
            totalDuration = mMinFormat.format(total);
            currentPosition = mMinFormat.format(currentProgress);
        }

        if (mTotalDuration != null) {
            mTotalDuration.setText(totalDuration);
        }

        //更新当前时间

        if (mCurrentPosition != null) {
            mCurrentPosition.setText(currentPosition);
        }


        if (!mIsUserTouch) {
            mSeekbar.setProgress((int) currentProgress);
        }
    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        this.mTrackTitleText = track.getTrackTitle();
        if (mTitle != null) {
            mTitle.setText(mTrackTitleText);
        }

        if (mTrackPageView != null) {
            mTrackPageView.setCurrentItem(playIndex, true);
        }


        //修改播放列表里的内容。
        if (mDPopWindow != null) {
            mDPopWindow.setCurrentPlayPosition(playIndex);
        }

    }
}
