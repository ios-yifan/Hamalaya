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

    private final View mPopView;
    private View mColseBtn;

    public DPopWindow() {
        //载入 view
        super(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置这个属性之前要先设置setBackgroundDrawable，否则无法关闭
        setOutsideTouchable(true);
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        setContentView(mPopView);

        // 设置窗口进入退出动画

        setAnimationStyle(R.style.pop_animation);
        
        
        
        initView();
        initEvent();
    }

    private void initView() {
        mColseBtn = mPopView.findViewById(R.id.play_list_close_btn);
    }

    private void initEvent() {
        mColseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击窗口消失
                DPopWindow.this.dismiss();
            }
        });

    }
}
