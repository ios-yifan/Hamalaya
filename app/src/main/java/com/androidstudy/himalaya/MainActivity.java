package com.androidstudy.himalaya;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidstudy.himalaya.adapters.IndicatorAdapter;
import com.androidstudy.himalaya.adapters.MainContentAdapter;
import com.androidstudy.himalaya.interfaces.IPlayerViewCallback;
import com.androidstudy.himalaya.presenters.PlayerPresenter;
import com.androidstudy.himalaya.presenters.RecommendPresenter;
import com.androidstudy.himalaya.utils.LogUtils;
import com.androidstudy.himalaya.views.RoundRectImageView;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;


public class MainActivity extends FragmentActivity implements IPlayerViewCallback {

    private static final String TAG = "MainActivity";
    private MagicIndicator magicIndicator;
    private ViewPager contentPager;
    private IndicatorAdapter indicatorAdapter;
    private RoundRectImageView mRoundRectImageView;
    private TextView mTitleTv;
    private TextView mAuthorTv;
    private ImageView mPlayControl;
    private PlayerPresenter mPlayerPresenter;
    private View mPlayControlItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();
        initPresenter();
    }

    private void initPresenter() {
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
    }

    /**
     * 响应推荐页头部按钮的点击事件
     */
    private void initEvent() {
        indicatorAdapter.setOnIndicatorTapClickListener(new IndicatorAdapter.OnIndicatorTapClickListener() {
            @Override
            public void onTabClick(int index) {
                LogUtils.d(TAG,"click index is >" + index);
                if (contentPager != null){
                    contentPager.setCurrentItem(index);
                }
            }
        });

        mPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean hasPlayList = mPlayerPresenter.hasPlayList();
                    if (!hasPlayList) {
                        //没有设置过播放
                        //设置第一个推荐
                        playFirstRecommend();
                    } else {
                        if (mPlayerPresenter.isPlay()) {
                            mPlayerPresenter.pause();
                        } else {
                            mPlayerPresenter.play();
                        }
                    }

                }
            }
        });

        mPlayControlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPlayList = mPlayerPresenter.hasPlayList();
                if (!hasPlayList) {
                    playFirstRecommend();
                }else {
                    startActivity(new Intent(MainActivity.this,PlayerActivity.class));
                }
            }
        });
    }

    /**
     * 第一个推荐
     */
    private void playFirstRecommend() {

        List<Album> currentRecommend = RecommendPresenter.getInstance().getCurrentRecommend();
        if (currentRecommend != null && currentRecommend.size() > 0) {

            Album album = currentRecommend.get(0);
            long id = album.getId();
            mPlayerPresenter.playByAlbumId(id);

        }

    }

    private void initView() {

        //初始化头部按钮和 viewpager
        contentPager = findViewById(R.id.content_page);
        magicIndicator = findViewById(R.id.main_indicator);

        // 头部按钮实现 adapter
        indicatorAdapter = new IndicatorAdapter(this);
        magicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));

        /*创建内容适配器*/
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager);
        contentPager.setAdapter(mainContentAdapter);
        /*创建 indicator 的适配器*/

        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true); //平分位置
        commonNavigator.setAdapter(indicatorAdapter);

        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator,contentPager);

        mRoundRectImageView = findViewById(R.id.track_cover);
        mTitleTv = findViewById(R.id.main_title_tv);
        mTitleTv.setSelected(true);
        mAuthorTv = findViewById(R.id.main_author_tv);
        mPlayControl = findViewById(R.id.play_control_iv);

        mPlayControlItem = findViewById(R.id.main_play_control_item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
    }

    private void updatePlayControl(boolean isPlaying){
        if (mPlayControl != null) {
            mPlayControl.setImageResource(isPlaying ? R.drawable.selector_player_pause:R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayStart() {

        updatePlayControl(true);
    }

    @Override
    public void onPlayPause() {
        updatePlayControl(false);
    }

    @Override
    public void onPlayStop() {
        updatePlayControl(false);
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

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(long currentProgress, long total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track != null) {
            String trackTitle = track.getTrackTitle();
            if (mTitleTv != null) {
                mTitleTv.setText(trackTitle);
            }
            String nickname = track.getAnnouncer().getNickname();
            if (mAuthorTv != null) {
                mAuthorTv.setText(nickname);
            }
            String coverUrlMiddle = track.getCoverUrlMiddle();
            Picasso.with(this).load(coverUrlMiddle).into(mPlayControl);
            Log.d(TAG, "onTrackUpdate: " + trackTitle + nickname + coverUrlMiddle);
        }

    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}