package com.hi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bm.library.PhotoView;
import com.hi.R;
import com.hi.imageloader.ImageLoaderManager;
import com.hi.nimsdk.NimHistorySDK;
import com.hi.utilslqr.Bimp;
import com.hi.utilslqr.FileUtils;
import com.hi.utilslqr.LogUtils;
import com.hi.utilslqr.UIUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * @描述 聊天图片查看界面(只查询本地记录)
 */
public class ImageWatchActivity extends BaseActivity {

    private String mAccount;
    private SessionTypeEnum mSessionType;
    private IMMessage mOriMessage;
    private IMMessage mAnchor;
    private List<IMMessage> mData = new ArrayList<>();
    private PhotoViewPagerAdapter mAdapter;

    private boolean isFirstLoad = true;
    private int mCurrentItem;
    private boolean mIsEditMode;//标记是否是从文件墙界面的编辑模式下调用的

    @Bind(R.id.root)
    RelativeLayout mRlRoot;
    @Bind(R.id.btnWatchOrigImage)
    Button mBtnWatchOrigImage;
    @Bind(R.id.vpImage)
    ViewPager mVpImage;
    @Bind(R.id.pbLoading)
    ProgressBar mPbLoading;
    @Bind(R.id.ivShowPic)
    ImageView mIvShowPic;


    @OnClick({R.id.ibWall, R.id.btnWatchOrigImage})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btnWatchOrigImage:
                loadOrignImage();
                break;
            case R.id.ibWall:
                //跳转到图片墙界面
                Intent intent = new Intent(this, FileWallActivity.class);
                intent.putExtra("account", mAccount);
                intent.putExtra("sessionType", mSessionType);
                intent.putExtra("currentMsg", mData.get(mVpImage.getCurrentItem()));
                startActivity(intent);
                break;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initView();
        initListener();
    }

    public void init() {
        Intent intent = getIntent();
        mAccount = intent.getStringExtra("account");
        mIsEditMode = intent.getBooleanExtra("isEditMode", false);
        mSessionType = (SessionTypeEnum) intent.getSerializableExtra("sessionType");
        mOriMessage = (IMMessage) intent.getSerializableExtra("message");
        mAnchor = MessageBuilder.createEmptyMessage(mAccount, mSessionType, 0);
        loadPreImage();
    }

    public void initView() {
        setContentView(R.layout.activity_image_watch);
        ButterKnife.bind(this);

        mAdapter = new PhotoViewPagerAdapter();
        mVpImage.setAdapter(mAdapter);
    }

    public void initListener() {
        mVpImage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    loadPreImage();
                }
                showBtnWatchOrignImage();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 显示底部“查看原图”按钮
     */
    private void showBtnWatchOrignImage() {
        //如果当前图是原图则隐藏底部按钮，否则显示
        int currentItem = mVpImage.getCurrentItem();
        ImageAttachment ia = (ImageAttachment) mData.get(currentItem).getAttachment();
        if (!TextUtils.isEmpty(ia.getPath())) {
            showWatchOrignBtn(false);
        } else {
            showWatchOrignBtn(true);
        }
    }

    /**
     * 每次从本地查询10张图片
     */
    private void loadPreImage() {

        LogUtils.sf("loadPreImage");

        NimHistorySDK.queryMessageListByType(MsgTypeEnum.image, mAnchor, 10).setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
            @Override
            public void onResult(int code, List<IMMessage> result, Throwable exception) {
                if (code != ResponseCode.RES_SUCCESS || exception != null || result == null || result.size() == 0) {
                    return;
                }

                Collections.reverse(result);

                //0、有查询数据就记录当前第一条消息
                mAnchor = result.get(0);

                //1、加载历史消息，筛选出图片消息
                List<IMMessage> tmpList = new ArrayList<>();
                for (int i = 0; i < result.size(); i++) {
                    IMMessage message = result.get(i);
                    if (message.getMsgType() == MsgTypeEnum.image) {
                        tmpList.add(message);
                    }
                }

                //2、如果在当前加载出来的消息中没有图片消息，则递归拉取
                if (tmpList.isEmpty()) {
                    loadPreImage();
                } else {
                    mData.addAll(0, tmpList);

                    //3、如果是第一次加载，还需要显示对应图片的位置
                    if (isFirstLoad)
                        for (int i = 0; i < result.size(); i++) {
                            IMMessage message = result.get(i);
                            if (message.isTheSame(mOriMessage)) {
                                mCurrentItem = i;
                                LogUtils.sf("第一次加载，还需要显示对应图片的位置:" + mCurrentItem);
                                break;
                            }
                        }
                    mAdapter.notifyDataSetChanged();
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            if (isFirstLoad) {
                                mVpImage.setCurrentItem(mCurrentItem, false);
                            } else {
                                mVpImage.setCurrentItem(mCurrentItem + mData.size(), false);
                            }
                            showBtnWatchOrignImage();
                        }
                    });
                    isFirstLoad = false;

                    //如果只有一张的话，则继续查询历史图片消息
                    if (mData.size() == 1) {
                        loadPreImage();
                    }
                }
            }
        });
    }

    class PhotoViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView pv = new PhotoView(ImageWatchActivity.this);
            pv.enable();//启动缩放
            pv.setScaleType(ImageView.ScaleType.FIT_CENTER);

            ImageAttachment ia = (ImageAttachment) mData.get(position).getAttachment();
            //本地没有原图
            if (TextUtils.isEmpty(ia.getPath())) {
                //先设置缩略图
                if (!TextUtils.isEmpty(ia.getThumbPath())) {
                    pv.setImageBitmap(Bimp.getLoacalBitmap(ia.getThumbPath()));
                }
                //再加载原图
                ImageLoaderManager.LoadNetImage(ia.getUrl(), pv);
            } else {
                pv.setImageBitmap(Bimp.getLoacalBitmap(ia.getPath()));
            }

            container.addView(pv);
            return pv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        private int mChildCount = 0;

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            if (mChildCount > 0) {
                mChildCount--;
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }
    }

    /**
     * 设置是否显示底部的“查看原图”按钮
     */
    public void showWatchOrignBtn(boolean show) {
        mBtnWatchOrigImage.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 加载原图
     */
    public void loadOrignImage() {
        mPbLoading.setVisibility(View.VISIBLE);
        mBtnWatchOrigImage.setEnabled(true);

        int currentItem = mVpImage.getCurrentItem();
        ImageAttachment ia = (ImageAttachment) mData.get(currentItem).getAttachment();

        //下载原图
        OkGo.get(ia.getUrl()).execute(new FileCallback(FileUtils.getDirFromPath(ia.getPathForSave()),
                FileUtils.getFileNameFromPath(ia.getPathForSave())) {
            @Override
            public void onSuccess(File file, Call call, Response response) {
                mBtnWatchOrigImage.setEnabled(true);
                showWatchOrignBtn(false);
                mPbLoading.setVisibility(View.GONE);
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                mBtnWatchOrigImage.setEnabled(true);
                showWatchOrignBtn(true);
//              mBtnWatchOrigImage.setVisibility(View.GONE);
                UIUtils.showToast("加载原图失败");
            }
        });
    }

}
