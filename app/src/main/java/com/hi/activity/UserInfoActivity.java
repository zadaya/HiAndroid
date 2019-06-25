package com.hi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hi.R;
import com.hi.imageloader.ImageLoaderManager;
import com.hi.model.Contact;
import com.hi.model.UserCache;
import com.hi.nimsdk.NimBlackListSDK;
import com.hi.nimsdk.NimFriendSDK;
import com.hi.nimsdk.NimUserInfoSDK;
import com.hi.utilslqr.UIUtils;
import com.hi.widget.OptionItemView;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @描述 详细资料界面
 */
public class UserInfoActivity extends BaseActivity {

    public static final String USER_INFO_ACCOUNT = "account";

    private Intent mIntent;
    private Animation mPushBottomInAnimation;
    private Animation mPushBottomOutAnimation;
    private String mAccount;
    private Contact mContact;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    //内容区
    @Bind(R.id.ivHeader)
    ImageView mIvHeader;
    @Bind(R.id.tvAlias)
    TextView mTvAlias;
    @Bind(R.id.tvAccount)
    TextView mTvAccount;
    @Bind(R.id.tvName)
    TextView mTvName;
    @Bind(R.id.ivGender)
    ImageView mIvGender;

    @Bind(R.id.oivAliasAndTag)
    OptionItemView mOivAliasAndTag;
    @Bind(R.id.llArea)
    LinearLayout mLlArea;
    @Bind(R.id.tvArea)
    TextView mTvArea;
    @Bind(R.id.llSignature)
    LinearLayout mLlSignature;
    @Bind(R.id.tvSignature)
    TextView mTvSignature;

    @Bind(R.id.btnCheat)
    Button mBtnCheat;
    @Bind(R.id.btnVideoCheat)
    Button mBtnVideoCheat;
    @Bind(R.id.btnAddFriend)
    Button mBtnAddFriend;

    //菜单区
    @Bind(R.id.rlMenu)
    RelativeLayout mRlMenu;
    @Bind(R.id.vMask)
    View mVMask;
    @Bind(R.id.svMenu)
    ScrollView mSvMenu;

    @OnClick({R.id.oivAliasAndTag, R.id.btnCheat, R.id.btnVideoCheat, R.id.btnAddFriend,
            R.id.oivAlias, R.id.oivFriendsCirclePrivacySet,R.id.oivMoveOutBlackList, R.id.oivAddToBlackList, R.id.oivDelete})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.oivAliasAndTag:
                jumpToAliasActivity();
                break;
            case R.id.btnCheat:
                setResult(RESULT_OK);
                mIntent = new Intent(this, SessionActivity.class);
                mIntent.putExtra(SessionActivity.SESSION_ACCOUNT, mAccount);
                startActivity(mIntent);
                finish();
                break;
            case R.id.btnVideoCheat:
                break;
            case R.id.btnAddFriend:
                //TODO
                mIntent = new Intent(this, PostscriptActivity.class);
                mIntent.putExtra("account", mAccount);
                startActivity(mIntent);
                break;
            case R.id.oivAlias://修改备注
//                jumpToAliasActivity();
                hideMenu();
                break;
            case R.id.oivAddToBlackList://加入黑名单
                hideMenu();
                showMaterialDialog("加入黑名单", "加入黑名单，你将不再收到对方的消息，并且你们互相看不到对方朋友圈的更新", "确定", "取消", new View
                        .OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NimBlackListSDK.addToBlackList(mAccount, new RequestCallback<Void>() {
                            @Override
                            public void onSuccess(Void param) {
                                UIUtils.showToast("加入黑名单成功");
                                Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailed(int code) {
                                UIUtils.showToast("加入黑名单失败" + code);
                            }

                            @Override
                            public void onException(Throwable exception) {
                                exception.printStackTrace();
                            }
                        });
                        hideMaterialDialog();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideMaterialDialog();
                    }
                });
                break;



            case R.id.oivMoveOutBlackList:    //移出黑名单
                hideMenu();
                showMaterialDialog("移出黑名单", "移出黑名单可重新与对方建立好友关系", "确定", "取消", new View
                        .OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NimBlackListSDK.removeFromBlackList(mAccount, new RequestCallback<Void>() {
                            @Override
                            public void onSuccess(Void param) {
                                UIUtils.showToast("移出黑名单成功");
                                Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailed(int code) {
                                UIUtils.showToast("加入黑名单失败" + code);
                            }

                            @Override
                            public void onException(Throwable exception) {
                                exception.printStackTrace();
                            }
                        });
                        hideMaterialDialog();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideMaterialDialog();
                    }
                });
                break;




            case R.id.oivDelete://删除好友
                hideMenu();
                showMaterialDialog("删除联系人", "将联系人" + mContact.getDisplayName() + "删除，将同时删除与联系人的聊天记录", "删除", "取消", new
                        View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //删除
                        NimFriendSDK.deleteFriend(mAccount, new RequestCallback<Void>() {
                            @Override
                            public void onSuccess(Void param) {
                                UIUtils.showToast("删除好友成功");
                                Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailed(int code) {
                                UIUtils.showToast("删除好友失败" + code);
                            }

                            @Override
                            public void onException(Throwable exception) {
                                exception.printStackTrace();
                            }
                        });
                        hideMaterialDialog();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消
                        hideMaterialDialog();
                    }
                });
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initView();
        initData();
    }

    private void jumpToAliasActivity() {
//        mIntent = new Intent(UserInfoActivity.this, AliasActivity.class);
//        mIntent.putExtra("contact", mContact);
//        startActivityForResult(mIntent, AliasActivity.REQ_CHANGE_ALIAS);
    }

    @OnClick(R.id.vMask)
    public void mask() {
        toggleMenu();
    }

    public void init() {
        mAccount = getIntent().getStringExtra("account");
        if (TextUtils.isEmpty(mAccount)) {
//            interrupt();
            return;
        }
    }

    public void initView() {
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        initToolbar();
        initAnimation();

        if (UserCache.getAccount().equals(mAccount)) {//自己
            mOivAliasAndTag.setVisibility(View.GONE);
            mLlArea.setVisibility(View.GONE);
//            mLlSignature.setVisibility(View.GONE);
            mBtnCheat.setVisibility(View.VISIBLE);
        } else {
            if (NimFriendSDK.isMyFriend(mAccount)) {
                mBtnCheat.setVisibility(View.VISIBLE);
//                mOivAliasAndTag.setVisibility(View.VISIBLE);
//                mBtnVideoCheat.setVisibility(View.VISIBLE);
                mBtnAddFriend.setVisibility(View.GONE);
            } else {
                mBtnCheat.setVisibility(View.GONE);
                mOivAliasAndTag.setVisibility(View.GONE);
//                mBtnVideoCheat.setVisibility(View.GONE);
                mBtnAddFriend.setVisibility(View.VISIBLE);
            }
        }
    }

    public void initData() {
        //根据账号得到好友信息
        if (NimFriendSDK.isMyFriend(mAccount)) {
            mContact = new Contact(mAccount);
            //获取好友信息并显示
            setUserInfo();
            //先信息旧信息，同时更新新的好友信息
            getUserInfoFromServer();
        } else {
            getUserInfoFromServer();
        }
    }

    private void getUserInfoFromServer() {
        NimUserInfoSDK.getUserInfoFromServer(mAccount, new RequestCallback<List<NimUserInfo>>() {
            @Override
            public void onSuccess(List<NimUserInfo> param) {
                if (param != null && param.size() > 0) {
                    mContact = new Contact(NimFriendSDK.getFriendByAccount(mAccount), param.get(0));
                    //获取好友信息并显示
                    setUserInfo();
                }
            }

            @Override
            public void onFailed(int code) {

            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (NimFriendSDK.isMyFriend(mAccount)) {
            new MenuInflater(this).inflate(R.menu.menu_more, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.itemMore:
                toggleMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AliasActivity.REQ_CHANGE_ALIAS && resultCode == RESULT_OK) {
            //修改备注成功
            initData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (mRlMenu.getVisibility() == View.VISIBLE) {
            //隐藏
            mSvMenu.startAnimation(mPushBottomOutAnimation);
            return;
        }
        super.onBackPressed();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("详细资料");
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
    }

    private void initAnimation() {
        mPushBottomInAnimation = AnimationUtils.loadAnimation(this, R.anim.push_bottom_in);
        mPushBottomOutAnimation = AnimationUtils.loadAnimation(this, R.anim.push_bottom_out);
        mPushBottomInAnimation.setDuration(300);
        mPushBottomOutAnimation.setDuration(300);
        mPushBottomOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRlMenu.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void toggleMenu() {
        if (mRlMenu.getVisibility() == View.VISIBLE) {
            //隐藏
            mSvMenu.startAnimation(mPushBottomOutAnimation);
        } else {
            //显示
            mRlMenu.setVisibility(View.VISIBLE);
            mSvMenu.startAnimation(mPushBottomInAnimation);
        }
    }

    private void hideMenu() {
        mSvMenu.startAnimation(mPushBottomOutAnimation);
    }

    private void setUserInfo() {
        //设置头像
        if (TextUtils.isEmpty(mContact.getAvatar())) {
            mIvHeader.setImageResource(R.mipmap.default_header);
        } else {
            ImageLoaderManager.LoadNetImage(mContact.getAvatar(), mIvHeader);
        }

        //设置性别
        NimUserInfo userInfo = mContact.getUserInfo();
        if (userInfo.getGenderEnum() == GenderEnum.FEMALE) {
            mIvGender.setImageResource(R.mipmap.ic_gender_female);
        } else if (userInfo.getGenderEnum() == GenderEnum.MALE) {
            mIvGender.setImageResource(R.mipmap.ic_gender_male);
        } else {
            mIvGender.setVisibility(View.GONE);
        }

        //判断是否有起备注
        if (TextUtils.isEmpty(mContact.getAlias())) {
//            mTvAlias.setVisibility(View.GONE);
            mTvName.setVisibility(View.GONE);
        } else {
//            mTvAlias.setVisibility(View.VISIBLE);
            mTvName.setVisibility(View.VISIBLE);
        }
        mTvAlias.setText(mContact.getDisplayName());
        mTvAccount.setText("账号:" + mContact.getAccount());
        mTvName.setText("昵称:" + mContact.getName());
//        Map<String, Object> extensionMap = mContact.getUserInfo().getExtensionMap();
//        if (extensionMap != null){
//            mTvArea.setText(StringUtils.isEmpty(extensionMap.get(AppConst.UserInfoExt.AREA)) ? "" : (String)
//                    extensionMap.get(AppConst.UserInfoExt.AREA));
//            mTvSignature.setText(mContact.getUserInfo().getSignature());
//        }
        mTvSignature.setText(mContact.getUserInfo().getSignature());
    }
}
