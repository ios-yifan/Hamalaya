package com.androidstudy.himalaya.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.androidstudy.himalaya.R;
import com.androidstudy.himalaya.base.BaseApplication;

public class DPopWindow extends PopupWindow {

    public DPopWindow() {
        //载入 view
        super(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置这个属性之前要先设置setBackgroundDrawable，否则无法关闭
        setOutsideTouchable(true);
        View popView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        setContentView(popView);

        // 设置窗口进入退出动画

        setAnimationStyle(R.style.pop_animation);
    }
}
