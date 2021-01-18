package com.androidstudy.himalaya.presenters;

import com.androidstudy.himalaya.api.XimalayaApi;
import com.androidstudy.himalaya.interfaces.ISearchCallBack;
import com.androidstudy.himalaya.interfaces.ISearchPresenter;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private List<ISearchCallBack> mSearchCallBacks = new ArrayList<>();
    //当前的搜索关键字
    private String mCurrentKeyword = null;
    private final XimalayaApi mXimalayaApi;
    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;

    private SearchPresenter(){

        mXimalayaApi = XimalayaApi.getXimalayaApi();
    }

    private static SearchPresenter sSearchPresenter = null;

    public static SearchPresenter getSearchPresenter(){

        if (sSearchPresenter == null){
            synchronized (SearchPresenter.class){
                sSearchPresenter = new SearchPresenter();
            }
        }
        return sSearchPresenter;
    }

    @Override
    public void doSearch(String keyword) {

        //用于重新搜索
        this.mCurrentKeyword = keyword;
        search(keyword);
    }

    private void search(String keyword) {
        mXimalayaApi.searchByKeyword(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                if (albums != null) {


                } else {

                }

                for (ISearchCallBack searchCallBack : mSearchCallBacks) {
                    searchCallBack.onSearchResultLoaded(albums);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyword);
    }

    @Override
    public void loaderMode() {

    }

    @Override
    public void getHotWord() {
        mXimalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> result = hotWordList.getHotWordList();
                    for (ISearchCallBack searchCallBack : mSearchCallBacks) {

                        searchCallBack.onHotWordLoaded(result);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

                for (ISearchCallBack searchCallBack : mSearchCallBacks) {
                    searchCallBack.onError(i,s);
                }
            }
        });
    }

    @Override
    public void getRecommendWork(String keyword) {

        mXimalayaApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();

                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallBack iSearchCallBack) {
        if (!mSearchCallBacks.contains(iSearchCallBack)) {
            mSearchCallBacks.add(iSearchCallBack);
        }
    }

    @Override
    public void unRegisterViewCallback(ISearchCallBack iSearchCallBack) {
        mSearchCallBacks.remove(iSearchCallBack);
    }
}
