package com.androidstudy.himalaya;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.androidstudy.himalaya.adapters.IndicatorAdapter;
import com.androidstudy.himalaya.adapters.MainContentAdapter;
import com.androidstudy.himalaya.utils.LogUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    private MagicIndicator magicIndicator;
    private ViewPager contentPager;
    private IndicatorAdapter indicatorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();
    }

    /**
     * 响应推荐页头部按钮的点击事件
     */
    private void initEvent() {
        indicatorAdapter.setOnIndicatorTapClickListener(new IndicatorAdapter.OnIndicatorTapClickListener() {
            @Override
            public void onTabClick(int index) {
                LogUtils.d(TAG,"click index is >" + index);
                if (contentPager != null){
                    contentPager.setCurrentItem(index);
                }
            }
        });
    }

    private void initView() {

        //初始化头部按钮和 viewpager
        contentPager = findViewById(R.id.content_page);
        magicIndicator = findViewById(R.id.main_indicator);

        // 头部按钮实现 adapter
        indicatorAdapter = new IndicatorAdapter(this);
        magicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));

        /*创建内容适配器*/
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager);
        contentPager.setAdapter(mainContentAdapter);
        /*创建 indicator 的适配器*/

        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true); //平分位置
        commonNavigator.setAdapter(indicatorAdapter);

        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator,contentPager);



    }


}