package com.androidstudy.himalaya;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidstudy.himalaya.adapters.DetailListAdapter;
import com.androidstudy.himalaya.base.BaseActivity;
import com.androidstudy.himalaya.interfaces.IAlbumDetailViewCallback;
import com.androidstudy.himalaya.presenters.AlbumDetailPresenter;
import com.androidstudy.himalaya.presenters.PlayerPresenter;
import com.androidstudy.himalaya.utils.ImageBlur;
import com.androidstudy.himalaya.views.RoundRectImageView;
import com.androidstudy.himalaya.views.UILoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener, DetailListAdapter.ItemClickListener {

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        albumDetailPresenter = AlbumDetailPresenter.getInstance();
        albumDetailPresenter.registerViewCallback(this);

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
    }

    private View createSuccessView(ViewGroup container) {

        View inflate = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        mDetailRecycle = inflate.findViewById(R.id.album_recycle);

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
        return inflate;
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
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
}
