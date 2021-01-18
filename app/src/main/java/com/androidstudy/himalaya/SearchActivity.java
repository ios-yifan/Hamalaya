package com.androidstudy.himalaya;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidstudy.himalaya.adapters.AlbumListAdapter;
import com.androidstudy.himalaya.base.BaseActivity;
import com.androidstudy.himalaya.interfaces.ISearchCallBack;
import com.androidstudy.himalaya.presenters.SearchPresenter;
import com.androidstudy.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity implements ISearchCallBack {

    private View mBackBtn;
    private EditText mInputBox;
    private View mSearchBtn;
    private FrameLayout mResultContainer;
    private SearchPresenter mSearchPresenter;
    private UILoader mLoader;
    private RecyclerView mResultList;
    private AlbumListAdapter mAlbumListAdapter;
    //    private FlowTextLayout mFlowTextLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSearchPresenter.unRegisterViewCallback(this);
    }

    private void initPresenter() {

        // 注册 UI 更新的接口
        mSearchPresenter = SearchPresenter.getSearchPresenter();
        mSearchPresenter.registerViewCallback(this);

        // 获取热词
        mSearchPresenter.getHotWord();
    }

    private void initEvent() {

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trim = mInputBox.getText().toString().trim();
                mSearchPresenter.doSearch(trim);
                mLoader.updateStatus(UILoader.UIStatus.LAODING);
            }
        });

        mInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
//            @Override
//            public void onItemClick(String text) {
//                Toast.makeText(SearchActivity.this,text,Toast.LENGTH_SHORT).show();
//            }
//        });

        mLoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                if (mSearchPresenter != null) {
                    mSearchPresenter.reSearch();
                    mLoader.updateStatus(UILoader.UIStatus.LAODING);
                }
            }
        });
    }

    private void initView() {

        mBackBtn = findViewById(R.id.search_back_btn);
        mInputBox = findViewById(R.id.search_input);
        mSearchBtn = findViewById(R.id.search_btn);
        mResultContainer = findViewById(R.id.search_container);

        if (mLoader == null){
            mLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }
            };
            if (mLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mLoader.getParent()).removeView(mLoader);
            }
            mResultContainer.addView(mLoader);
        }

//        mFlowTextLayout = findViewById(R.id.flow_text_layout);
    }

    //创建请求成功的 view
    private View createSuccessView() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.search_result_layout, null);
        mResultList = inflate.findViewById(R.id.result_list_view);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mResultList.setLayoutManager(linearLayoutManager);
        mAlbumListAdapter = new AlbumListAdapter();
        mResultList.setAdapter(mAlbumListAdapter);
        return inflate;
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {

        if (result != null) {

            if (result.size() == 0) {
                mLoader.updateStatus(UILoader.UIStatus.EMTRY);
            } else {
                //数据不为空
                mAlbumListAdapter.setData(result);
                mLoader.updateStatus(UILoader.UIStatus.SUCCESS);

            }
        }
    }

    @Override
    public void onHotWordLoaded(List<HotWord> hotWordList) {

        List<String> hotwords = new ArrayList<>();
        hotwords.clear();
        for (HotWord hotWord : hotWordList) {
            String searchWord = hotWord.getSearchword();
            hotwords.add(searchWord);
        }
        // 更新 UI
//        mFlowTextLayout.setTextContents(hotwords);
    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOK) {

    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWorkList) {

    }

    @Override
    public void onError(int errorCode, String errorMsg) {

        if (mLoader != null) {
            mLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }
}
