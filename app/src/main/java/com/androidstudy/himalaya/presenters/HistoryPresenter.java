package com.androidstudy.himalaya.presenters;

import com.androidstudy.himalaya.base.BaseApplication;
import com.androidstudy.himalaya.data.HistoryDao;
import com.androidstudy.himalaya.data.IHistoryDaoCallback;
import com.androidstudy.himalaya.interfaces.IHistoryCallback;
import com.androidstudy.himalaya.interfaces.IHistoryPresenter;
import com.androidstudy.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 历史数量最多一百，超过先删除最先加的，然后加最近的
 */
public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallback {

    private List<IHistoryCallback> mCallbacks = new ArrayList<>();
    private final HistoryDao mHistoryDao;
    private List<Track> mCurrentHistories = null;
    private Track mCurrentAddTrack = null;

    private HistoryPresenter(){
        mHistoryDao = new HistoryDao();
        mHistoryDao.setCallback(this);
        listHistories();
    }

    private static HistoryPresenter sHistoryPresenter = null;

    public static HistoryPresenter getInstance(){
        if (sHistoryPresenter == null) {
            synchronized (HistoryPresenter.class){
                if (sHistoryPresenter == null) {
                    sHistoryPresenter = new HistoryPresenter();
                }
            }
        }
        return sHistoryPresenter;
    }
    @Override
    public void listHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.listHistories();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private boolean isDoDelAsOutOfSize = false;
    @Override
    public void addHistory(Track track) {
        //需要判断是否有100条

        if (mCurrentHistories != null && mCurrentHistories.size() >= Constants.MAX_HISTORY_COUNT) {
            //先删除再添加
            isDoDelAsOutOfSize = true;
            this.mCurrentAddTrack = track;
            delHistory(mCurrentHistories.get(mCurrentHistories.size() - 1));
        } else {
            doAddHistory(track);
        }
    }

    private void doAddHistory(Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.addTracks(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.delHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void clearHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.clearHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallback(IHistoryCallback iHistoryCallback) {
        if (!mCallbacks.contains(iHistoryCallback)) {
            mCallbacks.add(iHistoryCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(IHistoryCallback iHistoryCallback) {
        mCallbacks.remove(iHistoryCallback);
    }

    @Override
    public void onHistoryAdd(boolean isSuccess) {
        listHistories();
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
        if (isDoDelAsOutOfSize && mCurrentAddTrack != null) {
            //添加当前数据
            isDoDelAsOutOfSize = false;
            addHistory(mCurrentAddTrack);
        } else {
            listHistories();
        }
    }

    @Override
    public void onHistoriesLoaded(List<Track> tracks) {
        this.mCurrentHistories = tracks;
        //通知UI更新数据
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (IHistoryCallback callback : mCallbacks) {
                    callback.onHistoryLoaded(tracks);
                }
            }
        });
    }

    @Override
    public void onHistoriesClear(boolean isDeleteSuccess) {
        listHistories();
    }
}
