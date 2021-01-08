package com.androidstudy.himalaya.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.androidstudy.himalaya.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

public class IndicatorAdapter extends CommonNavigatorAdapter {

    private final String[] stringArray;
    private OnIndicatorTapClickListener mOnTapClickListener;

    public IndicatorAdapter(Context context) {
        stringArray = context.getResources().getStringArray(R.array.indicator_name);
    }

    @Override
    public int getCount() {
        return stringArray.length;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
        simplePagerTitleView.setNormalColor(Color.parseColor("#aaffffff"));
        simplePagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
        simplePagerTitleView.setTextSize(18);
        simplePagerTitleView.setText(stringArray[index]);
        simplePagerTitleView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mOnTapClickListener != null) {
                    mOnTapClickListener.onTabClick(index);
                }
            }
        });
        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        linePagerIndicator.setColors(Color.WHITE);
        return linePagerIndicator;
    }

    /**
     * 通过这个 listener 来通知外部页面点击了头部按钮，外部需要通过这个接口来处理相关内容
     * @param listener
     */
    public void setOnIndicatorTapClickListener(OnIndicatorTapClickListener listener){
        this.mOnTapClickListener = listener;
    }

    public interface OnIndicatorTapClickListener{

        /**
         * 点击头部按钮触发此接口
         * @param index
         */
        void onTabClick(int index);
    }
}
