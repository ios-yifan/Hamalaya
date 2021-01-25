package com.androidstudy.himalaya;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidstudy.himalaya.adapters.AlbumListAdapter;
import com.androidstudy.himalaya.adapters.SearchRecommendAdapter;
import com.androidstudy.himalaya.base.BaseActivity;
import com.androidstudy.himalaya.interfaces.ISearchCallBack;
import com.androidstudy.himalaya.presenters.AlbumDetailPresenter;
import com.androidstudy.himalaya.presenters.SearchPresenter;
import com.androidstudy.himalaya.utils.Constants;
import com.androidstudy.himalaya.views.FlowTextLayout;
import com.androidstudy.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

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
    private FlowTextLayout mFlowTextLayout;
    private InputMethodManager mSystemService;
    private View mDelBtn;
    public static final int TIME_SHOW_IM = 500;
    private RecyclerView mSearchList;
    private SearchRecommendAdapter mSearchRecommendAdapter;
    private TwinklingRefreshLayout mRefreshLayout;

    private boolean mNeedSuggestWords = true;
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

        mSystemService = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);


        // 注册 UI 更新的接口
        mSearchPresenter = SearchPresenter.getSearchPresenter();
        mSearchPresenter.registerViewCallback(this);

        // 获取热词
        mSearchPresenter.getHotWord();
    }

    private void initEvent() {

        mAlbumListAdapter.setonRecommendItemClickListener(new AlbumListAdapter.onRecommendItemClickListener() {
            @Override
            public void onItemClick(int position, Album data) {
                AlbumDetailPresenter.getInstance().setTargetAlbum(data);
                //点击跳转到详情界面
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {


                if (mSearchPresenter != null) {
                    mSearchPresenter.loaderMode();
                }


            }
        });
        if (mSearchRecommendAdapter != null) {
            mSearchRecommendAdapter.setItemClickListener(new SearchRecommendAdapter.ItemClickListener() {
                @Override
                public void onItemClick(String keyword) {

                    doSearchByKeyword(keyword);

                    // 不需要相关联想

                }
            });
        }
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
                if (TextUtils.isEmpty(trim)) {
                    return;
                }
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


                if (TextUtils.isEmpty(s)) {
                    mSearchPresenter.getHotWord();
                    mDelBtn.setVisibility(View.GONE);

                } else {
                    mDelBtn.setVisibility(View.VISIBLE);
                    if (mNeedSuggestWords) {
                        // 触发联想查询
                        getSuggestWord(s.toString());
                    } else {
                        mNeedSuggestWords = true;
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                mNeedSuggestWords = false;

                doSearchByKeyword(text);
            }
        });

        mLoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                if (mSearchPresenter != null) {
                    mSearchPresenter.reSearch();
                    mLoader.updateStatus(UILoader.UIStatus.LAODING);
                }
            }
        });

        mDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputBox.setText("");
            }
        });
    }

    private void doSearchByKeyword(String keywork) {
        if (TextUtils.isEmpty(keywork)) {
            return;
        }
        // 热词放入输入框内
        // 发起搜索
        mInputBox.setText(keywork);
        mInputBox.setSelection(keywork.length());
        if (mSearchPresenter != null) {
            mSearchPresenter.doSearch(keywork);
        }

        //改变 UI 状态
        if (mLoader != null) {
            mLoader.updateStatus(UILoader.UIStatus.LAODING);
        }
    }

    /**
     * 获取联想的关键词
     *
     * @param keyword
     */
    private void getSuggestWord(String keyword) {
        if (mSearchPresenter != null) {
            mSearchPresenter.getRecommendWork(keyword);
        }
    }

    private void initView() {

        mBackBtn = findViewById(R.id.search_back_btn);
        mInputBox = findViewById(R.id.search_input);
        mDelBtn = findViewById(R.id.search_input_delete);
        mDelBtn.setVisibility(View.GONE);

        mInputBox.postDelayed(new Runnable() {
            @Override
            public void run() {
                mInputBox.requestFocus();
                mSystemService.showSoftInput(mInputBox, InputMethodManager.SHOW_IMPLICIT);
            }
        }, TIME_SHOW_IM);
        mSearchBtn = findViewById(R.id.search_btn);
        mResultContainer = findViewById(R.id.search_container);

        if (mLoader == null) {
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

        //刷新控件
        mRefreshLayout = inflate.findViewById(R.id.search_result_refresh_layout);
        mRefreshLayout.setEnableRefresh(false);

        mRefreshLayout.setOverScrollBottomShow(false);
        //显示热词
        mFlowTextLayout = inflate.findViewById(R.id.recommend_hot_word);

        mResultList = inflate.findViewById(R.id.result_list_view);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mResultList.setLayoutManager(linearLayoutManager);
        mResultList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        mAlbumListAdapter = new AlbumListAdapter();
        mResultList.setAdapter(mAlbumListAdapter);

        //搜索推荐
        mSearchList = inflate.findViewById(R.id.search_recommend_list);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        mSearchList.setLayoutManager(linearLayoutManager1);
        mSearchList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        mSearchRecommendAdapter = new SearchRecommendAdapter();
        mSearchList.setAdapter(mSearchRecommendAdapter);

        return inflate;
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {
        handlerSearchResult(result);
        mSystemService.hideSoftInputFromWindow(this.mInputBox.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void handlerSearchResult(List<Album> result) {
        hideSuccessView();
        mRefreshLayout.setVisibility(View.VISIBLE);
        //隐藏键盘
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

        hideSuccessView();
        mFlowTextLayout.setVisibility(View.VISIBLE);
        if (mLoader != null) {
            mLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        List<String> hotwords = new ArrayList<>();
        hotwords.clear();
        for (HotWord hotWord : hotWordList) {
            String searchWord = hotWord.getSearchword();
            hotwords.add(searchWord);
        }
        // 更新 UI
        mFlowTextLayout.setTextContents(hotwords);
    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOK) {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
        }

        if (isOK) {
            handlerSearchResult(result);
        } else {
            Toast.makeText(SearchActivity.this,"没有更多...",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWorkList) {

        //联想相关的关键字
        if (mSearchRecommendAdapter != null) {
            mSearchRecommendAdapter.setData(keyWorkList);
        }
        //控制 UI 的状态
        if (mLoader != null) {
            mLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }

        // 控制显示和隐藏
        hideSuccessView();
        mSearchList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(int errorCode, String errorMsg) {

        if (mLoader != null) {
            mLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    private void hideSuccessView() {
        mRefreshLayout.setVisibility(View.GONE);
        mFlowTextLayout.setVisibility(View.GONE);
        mSearchList.setVisibility(View.GONE);
    }
}
