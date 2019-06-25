package com.hi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hi.R;
import com.hi.app.AppConst;
import com.hi.factory.ThreadPoolFactory;
import com.hi.imageloader.ImageLoaderManager;
import com.hi.nimsdk.NimTeamSDK;
import com.hi.nimsdk.NimUserInfoSDK;
import com.hi.utilslqr.UIUtils;
import com.lqr.ninegridimageview.LQRNineGridImageView;
import com.lqr.ninegridimageview.LQRNineGridImageViewAdapter;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

/**
 * @描述 二级码名片
 */
public class QRCodeCardActivity extends BaseActivity {

    public static final String QRCODE_USER = "code_user";
    public static final String QRCODE_TEAM = "code_team";

    private NimUserInfo mUser;
    private Team mTeam;
    private boolean isUserInfoQRcode = true;
    private LQRNineGridImageViewAdapter<NimUserInfo> mNineGridAdapter;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.ivHeader)
    ImageView mIvHeader;
    @Bind(R.id.ngiv)
    LQRNineGridImageView mNgivHeader;
    @Bind(R.id.tvName)
    TextView mTtvName;
    @Bind(R.id.ivGender)
    ImageView mIvGender;
    @Bind(R.id.ivCard)
    ImageView mIvCard;
    @Bind(R.id.tvTip)
    TextView mTvTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initView();
    }

    public void init() {
        Intent intent = getIntent();
        mUser = (NimUserInfo) intent.getSerializableExtra(QRCODE_USER);
        mTeam = (Team) intent.getSerializableExtra(QRCODE_TEAM);
        if (mUser == null && mTeam == null)
//            interrupt();
              return;
        if (mUser != null && mTeam == null) {
            isUserInfoQRcode = true;
        } else {
            isUserInfoQRcode = false;
        }
    }

    public void initView() {
        setContentView(R.layout.activity_qrcode_card);
        ButterKnife.bind(this);
        initToolbar();
        mNineGridAdapter = new LQRNineGridImageViewAdapter<NimUserInfo>() {
            @Override
            protected void onDisplayImage(Context context, ImageView imageView, NimUserInfo userInfo) {
                if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                    ImageLoaderManager.LoadNetImage(userInfo.getAvatar(), imageView);
                } else {
                    imageView.setImageResource(R.mipmap.default_header);
                }
            }
        };

        if (isUserInfoQRcode) {
            mIvHeader.setVisibility(View.VISIBLE);
            mNgivHeader.setVisibility(View.GONE);
            final String avatar = mUser.getAvatar();

            if (!TextUtils.isEmpty(avatar)) {
                ImageLoaderManager.LoadNetImage(avatar, mIvHeader);
            } else {
                mIvHeader.setImageResource(R.mipmap.default_header);
            }
            mTtvName.setText(mUser.getName());
            mTvTip.setText("扫一扫上面的二维码图案，加我账号");
            if (mUser.getGenderEnum() == GenderEnum.FEMALE) {
                mIvGender.setImageResource(R.mipmap.ic_gender_female);
            } else if (mUser.getGenderEnum() == GenderEnum.MALE) {
                mIvGender.setImageResource(R.mipmap.ic_gender_male);
            } else {
                mIvGender.setVisibility(View.GONE);
            }
        } else {
            mIvHeader.setVisibility(View.GONE);
            mNgivHeader.setVisibility(View.VISIBLE);
            mTtvName.setText(TextUtils.isEmpty(mTeam.getName()) ?
                    "群聊(" + mTeam.getMemberCount() + ")" : mTeam.getName());
            mIvGender.setVisibility(View.GONE);
            mTvTip.setText("扫一扫上面的二维码图案，加入群聊");
            //设置群聊的头像
            NimTeamSDK.queryMemberList(mTeam.getId(), new RequestCallback<List<TeamMember>>() {
                @Override
                public void onSuccess(List<TeamMember> memberList) {
                    if (memberList != null && memberList.size() > 0) {
                        List<String> accounts = new ArrayList<>();
                        int count = memberList.size() > 9 ? 9 : memberList.size();
                        for (int i = 0; i < count; i++) {
                            accounts.add(memberList.get(i).getAccount());
                        }
                        NimUserInfoSDK.getUserInfosFormServer(accounts, new RequestCallback<List<NimUserInfo>>() {
                            @Override
                            public void onSuccess(List<NimUserInfo> result) {
                                mNgivHeader.setAdapter(mNineGridAdapter);
                                mNgivHeader.setImagesData(result);
                            }

                            @Override
                            public void onFailed(int code) {

                            }

                            @Override
                            public void onException(Throwable exception) {

                            }
                        });
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
        createQRCode();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        new MenuInflater(this).inflate(R.menu.menu_more, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.itemMore:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(isUserInfoQRcode ? "二维码名片" : "群二维码名片");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
    }

    private void createQRCode() {
        ThreadPoolFactory.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                String content = isUserInfoQRcode ? AppConst.QRCodeCommend.ACCOUNT + mUser.getAccount() : AppConst.QRCodeCommend.TEAMID + mTeam.getId();
                final Bitmap codeWithLogo5 = QRCodeEncoder.syncEncodeQRCode(content, UIUtils.dip2Px(200));
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        mIvCard.setImageBitmap(codeWithLogo5);
                    }
                });
            }
        });
    }
}
