package com.androidstudy.himalaya.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.androidstudy.himalaya.R;
import com.androidstudy.himalaya.base.BaseApplication;

public abstract class UILoader extends FrameLayout {

    private View mLoadingView;
    private View mSuccessView;
    private View mNetWorkErrorView;
    private View mEmrtyView;
    private OnRetryClickListener retryListener = null;


    public enum UIStatus{
        LAODING,SUCCESS,NETWORK_ERROR,EMTRY,NONE
    }

    public UIStatus mCurrentStatus = UIStatus.NONE;

    public UILoader(@NonNull Context context) {
        this(context,null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        switchUIByCurrentStatus();
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                switchUIByCurrentStatus();
            }
        });
    }

    public void updateStatus(UIStatus status){
        mCurrentStatus = status;
        //更新 UI
        switchUIByCurrentStatus();
    }

    private void switchUIByCurrentStatus() {

        //加载中
        if (mLoadingView == null) {
            mLoadingView = getLoadingView();
            addView(mLoadingView);
        }
        mLoadingView.setVisibility(mCurrentStatus==UIStatus.LAODING?VISIBLE:GONE);

        //成功
        if (mSuccessView == null) {
            mSuccessView = getSuccessView(this);
            addView(mSuccessView);
        }
        mSuccessView.setVisibility(mCurrentStatus==UIStatus.SUCCESS?VISIBLE:GONE);

        //网络错误
        if (mNetWorkErrorView == null) {
            mNetWorkErrorView = getNetWorkError();
            addView(mNetWorkErrorView);
        }
        mNetWorkErrorView.setVisibility(mCurrentStatus==UIStatus.NETWORK_ERROR?VISIBLE:GONE);


        //数据为空
        if (mEmrtyView == null) {
            mEmrtyView = getEntryView();
            addView(mEmrtyView);
        }
        mEmrtyView.setVisibility(mCurrentStatus==UIStatus.EMTRY?VISIBLE:GONE);

    }

    private View getEntryView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_entry_view,this,false);
    }

    private View getNetWorkError() {
        View erroView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_error_view,this,false);
        erroView.findViewById(R.id.network_error_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (retryListener != null) {
                    retryListener.onRetryClick();
                }
            }
        });
        return erroView;
    }

    protected abstract View getSuccessView(ViewGroup container);

    private View getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_loading_view,this,false);

    }

    public void setOnRetryClickListener(OnRetryClickListener listener){
        this.retryListener = listener;
    }

    public interface OnRetryClickListener{
        void onRetryClick();
    }


}
