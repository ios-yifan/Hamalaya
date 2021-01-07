package com.androidstudy.himalaya;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
import com.androidstudy.himalaya.utils.ImageBlur;
import com.androidstudy.himalaya.views.RoundRectImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback {

    private ImageView largeCover;
    private AlbumDetailPresenter albumDetailPresenter;
    private RoundRectImageView smallCover;
    private TextView albumTitle;
    private TextView albumAuthor;
    private int currentPage = 1;
    private RecyclerView mDetailRecycle;
    private DetailListAdapter mDetailListAdapter;

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
        largeCover = findViewById(R.id.iv_large_cover);
        smallCover = findViewById(R.id.iv_small_cover);
        albumTitle = findViewById(R.id.tv_title);
        albumAuthor = findViewById(R.id.tv_author);
        mDetailRecycle = findViewById(R.id.album_recycle);

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
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {

        // 更新 UI 数据
        mDetailListAdapter.setData(tracks);

    }

    @Override
    public void onAlbumLoaded(Album album) {
        //获取专辑的详细内容
        albumDetailPresenter.getAlbumDetail((int) album.getId(),currentPage);

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
}
