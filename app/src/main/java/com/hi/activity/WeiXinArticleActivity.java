package com.hi.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.hi.R;
import com.hi.adapter.TabViewPagerAdapter;
import com.hi.fragment.WeiXinArticleFragment;
import com.hi.fragment.WeiXinPublicFragment;
import com.hi.widget.Topbar;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 微信文章
 */

public class WeiXinArticleActivity extends BaseActivity {


    @Bind(R.id.topbar_weixin)
    Topbar topbarWeixin;
    @Bind(R.id.tab_weixin_title)
    TabLayout tabWeixinTitle;
    @Bind(R.id.vp_weixin)
    ViewPager vpWeixin;
    private ArrayList<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weixin_article);
        ButterKnife.bind(this);

        initView();

        topbarWeixin.setTopbarOnClickListener(new Topbar.TopbarOnClickListener() {
            @Override
            public void leftOnClick() {
                finish();
            }

            @Override
            public void rightOnClick() {
            }
        });
    }

    private void initView() {
        mFragments = new ArrayList<>();
        ArrayList<String> tabList = new ArrayList<>();
        tabList.add("精选文章");
        tabList.add("公众号");
        for (int i =0; i<tabList.size();i++){
            tabWeixinTitle.addTab(tabWeixinTitle.newTab().setText(tabList.get(i)));
        }
        mFragments.add(new WeiXinArticleFragment());
        mFragments.add(new WeiXinPublicFragment());
        tabWeixinTitle.setTabMode(TabLayout.MODE_FIXED);
        TabViewPagerAdapter TopAdapter = new
                TabViewPagerAdapter(getSupportFragmentManager(), mFragments,tabList);
        vpWeixin.setAdapter(TopAdapter);
        tabWeixinTitle.setupWithViewPager(vpWeixin);

    }

}
