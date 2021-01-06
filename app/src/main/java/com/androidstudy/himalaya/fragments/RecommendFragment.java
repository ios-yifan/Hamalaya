package com.androidstudy.himalaya.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidstudy.himalaya.R;
import com.androidstudy.himalaya.base.BaseFragment;

public class RecommendFragment extends BaseFragment {

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView = layoutInflater.inflate(R.layout.fragment_recomment,container,false);
        return rootView;
    }
}
