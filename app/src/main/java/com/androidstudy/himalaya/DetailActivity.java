package com.androidstudy.himalaya;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidstudy.himalaya.adapters.DetailListAdapter;
import com.androidstudy.himalaya.base.BaseActivity;
import com.androidstudy.himalaya.base.BaseApplication;
import com.androidstudy.himalaya.interfaces.IAlbumDetailViewCallback;
import com.androidstudy.himalaya.interfaces.IPlayerViewCallback;
import com.androidstudy.himalaya.interfaces.ISubscriptionCallback;
import com.androidstudy.himalaya.presenters.AlbumDetailPresenter;
import com.androidstudy.himalaya.presenters.PlayerPresenter;
import com.androidstudy.himalaya.presenters.SubscriptionPresenter;
import com.androidstudy.himalaya.utils.ImageBlur;
import com.androidstudy.himalaya.views.RoundRectImageView;
import com.androidstudy.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.BezierPagerIndicator;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener, DetailListAdapter.ItemClickListener, IPlayerViewCallback, ISubscriptionCallback {

    private ImageView largeCover;
    private AlbumDetailPresenter albumDetailPresenter;
    private RoundRectImageView smallCover;
    private TextView albumTitle;
    private TextView albumAuthor;
    private int currentPage = 1;
    private RecyclerView mDetailRecycle;
    private DetailListAdapter mDetailListAdapter;
    private FrameLayout mDetailListContainer;
    private UILoader mUiLoader;

    private long mCurrentId = -1;
    private ImageView mPlayControl;
    private TextView mMPlayControlTips;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mCurrentTracks = null;
    private final static int DEFAULT_PLAY_POSITION = 0;
    private TwinklingRefreshLayout mTwinklingRefreshLayout;
    private String mTrackTitle;
    private TextView mSubBtn;
    private SubscriptionPresenter mSubscriptionPresenter;
    private Album mCurrentAlbum;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        //专辑详情
        initPresenter();

        //设置订阅按钮的状态
        updateSubState();

        updatePlayState(mPlayerPresenter.isPlay());
        initListener();
    }

    private void updateSubState() {
        if (mSubscriptionPresenter != null) {
            boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
            mSubBtn.setText(isSub ? R.string.cancel_sub_tips_text : R.string.sub_tip_text);
        }
    }

    private void initPresenter() {
        albumDetailPresenter = AlbumDetailPresenter.getInstance();
        albumDetailPresenter.registerViewCallback(this);

        //播放器
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);

        //订阅相关的 presenter
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.getSubscriptionList();
        mSubscriptionPresenter.registerViewCallback(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unRegisterViewCallback(this);
        }

        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }

        if (albumDetailPresenter != null) {
            albumDetailPresenter.unRegisterViewCallback(this);
        }
    }

    private void updatePlayState(boolean play) {
        if (play){
            //修改图标，文字修改为正在播放
            if (mPlayControl != null && mMPlayControlTips != null) {
                mPlayControl.setImageResource(R.drawable.selector_play_control_pause);
                if (!TextUtils.isEmpty(mTrackTitle)) {
                    mMPlayControlTips.setText(mTrackTitle);
                }
            }
        }else {
            if (mPlayControl != null && mMPlayControlTips != null) {
                mPlayControl.setImageResource(R.drawable.selector_play_control_play);
                mMPlayControlTips.setText(R.string.pause_text);
            }
        }
    }

    private void initListener() {
        mPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断播放器是否有播放列表
                if (mPlayerPresenter != null) {
                    boolean hasPlayList = mPlayerPresenter.hasPlayList();
                    if (hasPlayList) {
                        handlePlayControl();
                    } else {
                        handleNoPlayList();
                    }

                }
            }
        });

        mSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubscriptionPresenter != null) {
                    boolean sub = mSubscriptionPresenter.isSub(mCurrentAlbum);
                    //如果没有订阅，就订阅，如果订阅了，就取消
                    if (sub) {
                        mSubscriptionPresenter.deleteSubscription(mCurrentAlbum);
                    } else {
                        mSubscriptionPresenter.addSubscription(mCurrentAlbum);
                    }
                }
            }
        });
    }

    /**
     * 播放列表为空
     */
    private void handleNoPlayList() {
        mPlayerPresenter.setPlayList(mCurrentTracks,DEFAULT_PLAY_POSITION);
    }

    private void handlePlayControl() {
        //控制播放器状态
        if (mPlayerPresenter.isPlay()) {
            //正在播放
            mPlayerPresenter.pause();
        }else {
            //暂停
            mPlayerPresenter.play();
        }
    }

    private void initView() {

        mDetailListContainer = findViewById(R.id.detail_list_container);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
            mDetailListContainer.removeAllViews();
            mDetailListContainer.addView(mUiLoader);
            mUiLoader.setOnRetryClickListener(this);
        }


        largeCover = findViewById(R.id.iv_large_cover);
        smallCover = findViewById(R.id.iv_small_cover);
        albumTitle = findViewById(R.id.tv_title);
        albumAuthor = findViewById(R.id.tv_author);

        mPlayControl = findViewById(R.id.detail_play_control_iv);
        mMPlayControlTips = findViewById(R.id.detail_play_tv);
        mMPlayControlTips.setSelected(true);

        mSubBtn = findViewById(R.id.detail_subscription_btn);

    }

    private boolean isLoadMore = false;

    private View createSuccessView(ViewGroup container) {

        View inflate = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        mDetailRecycle = inflate.findViewById(R.id.album_recycle);

        mTwinklingRefreshLayout = inflate.findViewById(R.id.refresh_layout);


        //1.设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDetailRecycle.setLayoutManager(layoutManager);

        //2.设置适配器
        mDetailListAdapter = new DetailListAdapter();
        mDetailRecycle.setAdapter(mDetailListAdapter);

        //设置item 间距
        mDetailRecycle.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });

        mDetailListAdapter.setItemClickListener(this);
//        BezierPagerIndicator bezierPagerIndicator = new BezierPagerIndicator(this);
        BezierLayout headerView  = new BezierLayout(this);
        mTwinklingRefreshLayout.setHeaderView(headerView);
        mTwinklingRefreshLayout.setHeaderHeight(100);
        mTwinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                BaseApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTwinklingRefreshLayout.finishRefreshing();
                    }
                },2000);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                albumDetailPresenter.loadMore();
                isLoadMore = true;
            }
        });
        return inflate;
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        if (isLoadMore && mTwinklingRefreshLayout != null){
            mTwinklingRefreshLayout.finishLoadmore();
            isLoadMore = false;
        }
        this.mCurrentTracks = tracks;
        //根据结果显示 UI
        if (tracks == null || tracks.size() == 0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMTRY);
            }
        }
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }

        // 更新 UI 数据
        mDetailListAdapter.setData(tracks);
    }

    @Override
    public void onNetworkError(int i, String s) {
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    @Override
    public void onAlbumLoaded(Album album) {

        this.mCurrentAlbum = album;
        long id = album.getId();
        mCurrentId = id;
        //获取专辑的详 细内容

        if (albumDetailPresenter != null) {
            albumDetailPresenter.getAlbumDetail((int) id,currentPage);
        }

        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LAODING);
        }
        if (albumTitle != null) {
            albumTitle.setText(album.getAlbumTitle());
        }

        if (albumAuthor != null) {
            albumAuthor.setText(album.getAnnouncer().getNickname());
        }

        if (largeCover != null) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(largeCover, new Callback() {
                @Override
                public void onSuccess() {
                    ImageBlur.makeBlur(largeCover,DetailActivity.this);
                }

                @Override
                public void onError() {
                }
            });
        }

        if (smallCover != null) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(smallCover);
        }

    }

    @Override
    public void onLoaderMoreFinished(int size) {
        if (size > 0 ){
            Toast.makeText(this,"成功加载" + size + "条",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"没有更多",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onRefreshFinished(int size) {

    }

    //用户网络不佳点击重新加载
    @Override
    public void onRetryClick() {

        if (albumDetailPresenter != null) {
            albumDetailPresenter.getAlbumDetail((int) mCurrentId,currentPage);
        }
    }

    @Override
    public void onItemClick(List<Track> list, int position) {
        //设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(list,position);
        Intent intent = new Intent(this,PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPlayStart() {
        //修改图标，文字修改为正在播放
        updatePlayState(true);
    }

    @Override
    public void onPlayPause() {
        updatePlayState(false);
    }

    @Override
    public void onPlayStop() {
        updatePlayState(false);
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
            mTrackTitle = track.getTrackTitle();
            if (!TextUtils.isEmpty(mTrackTitle)) {
                mMPlayControlTips.setText(mTrackTitle);
            }
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }

    @Override
    public void onAddResult(boolean isSuccess) {

        if (isSuccess) {
            // 如果成功就修改 UI 成取消订阅
            mSubBtn.setText(R.string.cancel_sub_tips_text);
        }
        Toast.makeText(this,"订阅成功",Toast.LENGTH_SHORT).show();

        //
    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        if (isSuccess) {
            // 如果成功就修改 UI 成取消订阅
            mSubBtn.setText(R.string.sub_tip_text);
        }
        Toast.makeText(this,"取消订阅成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscriptionsLoaded(List<Album> albumList) {

        // 当前界面不需要处理
    }

    @Override
    public void onSubTooMany() {

        Toast.makeText(this,"订阅满了",Toast.LENGTH_SHORT).show();
    }
}
