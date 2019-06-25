package com.hi.fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hi.R;
import com.hi.adapter.NewsAdapter;
import com.hi.app.AppConst;
import com.hi.imageloader.GlideImageLoader;
import com.hi.model.NewsModel;
import com.hi.parse.JsonCallback;
import com.lzy.ninegrid.NineGridView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 新闻资讯
 */
public class NewsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener {

    private static final int PAGELIMIT = 10;
    private String mType;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private Context context;
    private NewsAdapter newsAdapter;
    private boolean isInitCache = false;
    private int mCurrentPage;

    public NewsFragment(String type) {
        this.mType = type;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        NineGridView.setImageLoader(new GlideImageLoader());
        super.onAttach(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(getActivity(),R.layout.frag_news, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        newsAdapter = new NewsAdapter(null);
        newsAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        newsAdapter.isFirstOnly(false);
        recyclerView.setAdapter(newsAdapter);

        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refreshLayout.setOnRefreshListener(this);
        newsAdapter.setOnLoadMoreListener(this);

        //开启loading,获取数据
        setRefreshing(true);
        onRefresh();
    }

    /** 下拉刷新 */
    @Override
    public void onRefresh() {
        mCurrentPage = 1;
        OkGo.get(AppConst.TIANXIN_NEWS+mType)//
            .params("key", AppConst.TIANXINKEY)//
            .params("page",mCurrentPage)
            .params("rand",1)
            .params("num",PAGELIMIT)
            .cacheKey("TabFragment_" + this)       //由于该fragment会被复用,必须保证key唯一,否则数据会发生覆盖
            .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)  //缓存模式先使用缓存,然后使用网络数据
            .execute(new JsonCallback<NewsModel>() {
                @Override
                public void onSuccess(NewsModel newsResponse, Call call, Response response) {
                    List<NewsModel.NewslistBean> datas =  newsResponse.getNewslist();
                    if (datas.size() != 0){
//                            currentPage = datas;
                        newsAdapter.setNewData(datas);
                    }
                }

                @Override
                public void onCacheSuccess(NewsModel newsResponse, Call call) {
                    //一般来说,只需呀第一次初始化界面的时候需要使用缓存刷新界面,以后不需要,所以用一个变量标识
                    if (!isInitCache) {
                        //一般来说,缓存回调成功和网络回调成功做的事情是一样的,所以这里直接回调onSuccess
                        onSuccess(newsResponse, call, null);
                        isInitCache = true;
                    }
                }

                @Override
                public void onCacheError(Call call, Exception e) {
                    //获取缓存失败的回调方法,一般很少用到,需要就复写,不需要不用关心
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    //网络请求失败的回调,一般会弹个Toast
                    showToast(e.getMessage());
                }

                @Override
                public void onAfter(@Nullable NewsModel newsResponse, @Nullable Exception e) {
                    super.onAfter(newsResponse, e);
                    //可能需要移除之前添加的布局
                    newsAdapter.removeAllFooterView();
                    //最后调用结束刷新的方法
                    setRefreshing(false);
                }
            });
    }

    /** 上拉加载 */
    @Override
    public void onLoadMoreRequested() {
        mCurrentPage = mCurrentPage +1;
        OkGo.get(AppConst.TIANXIN_NEWS+mType)//
                .params("key", AppConst.TIANXINKEY)//
                .params("page",mCurrentPage)
                .params("rand",1)
                .params("num",PAGELIMIT)
                .cacheMode(CacheMode.NO_CACHE)       //上拉不需要缓存
                .execute(new JsonCallback<NewsModel>() {
                    @Override
                    public void onSuccess(NewsModel newsResponse, Call call, Response response) {
                        if (newsResponse.getNewslist().size() != 0){   //防止崩溃
                            newsAdapter.addData(newsResponse.getNewslist());

                            //显示没有更多数据
                            if (newsResponse.getNewslist().size() == 0) {
                                newsAdapter.loadComplete();         //加载完成
                                View noDataView = View.inflate(getActivity(),R.layout.item_no_data, (ViewGroup)
                                        recyclerView.getParent());
                                newsAdapter.addFooterView(noDataView);
                            }
                        }else{
                            newsAdapter.loadComplete();         //加载完成
//                            T.showShort(getActivity(),"返回结果为null");
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        //显示数据加载失败,点击重试
                        newsAdapter.showLoadMoreFailedView();
                        //网络请求失败的回调,一般会弹个Toast
                        showToast(e.getMessage());
                    }
                });
    }

    public void showToast(String msg) {
        Snackbar.make(recyclerView, msg, Snackbar.LENGTH_SHORT).show();
    }

    public void setRefreshing(final boolean refreshing) {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(refreshing);
            }
        });
    }
}