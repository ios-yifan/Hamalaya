package com.androidstudy.himalaya;

import android.content.Context;
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
import com.androidstudy.himalaya.presenters.SearchPresenter;
import com.androidstudy.himalaya.views.FlowTextLayout;
import com.androidstudy.himalaya.views.UILoader;
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

        if (mSearchRecommendAdapter != null) {
            mSearchRecommendAdapter.setItemClickListener(new SearchRecommendAdapter.ItemClickListener() {
                @Override
                public void onItemClick(String keyword) {

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
                    // 触发联想查询
                    getSuggestWord(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                // 热词放入输入框内
                // 发起搜索
                mInputBox.setText(text);
                mInputBox.setSelection(text.length());
                if (mSearchPresenter != null) {
                    mSearchPresenter.doSearch(text);
                }

                //改变 UI 状态
                if (mLoader != null) {
                    mLoader.updateStatus(UILoader.UIStatus.LAODING);
                }
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

    /**
     * 获取联想的关键词
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
                mSystemService.showSoftInput(mInputBox,InputMethodManager.SHOW_IMPLICIT);
            }
        },TIME_SHOW_IM);
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

        //显示热词

        mFlowTextLayout = inflate.findViewById(R.id.recommend_hot_word);

        mResultList = inflate.findViewById(R.id.result_list_view);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mResultList.setLayoutManager(linearLayoutManager);
        mResultList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
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
                outRect.top = UIUtil.dip2px(view.getContext(),2);
                outRect.bottom = UIUtil.dip2px(view.getContext(),2);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });
        mSearchRecommendAdapter = new SearchRecommendAdapter();
        mSearchList.setAdapter(mSearchRecommendAdapter);

        return inflate;
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {
        hideSuccessView();
        mResultList.setVisibility(View.VISIBLE);
        //隐藏键盘
        mSystemService = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        mSystemService.hideSoftInputFromWindow(this.mInputBox.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
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

    private void hideSuccessView(){
        mResultList.setVisibility(View.GONE);
        mFlowTextLayout.setVisibility(View.GONE);
        mSearchList.setVisibility(View.GONE);
    }
}
