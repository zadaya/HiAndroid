package com.hi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hi.R;
import com.hi.nimsdk.NimUserInfoSDK;
import com.hi.utils.T;
import com.hi.utilslqr.KeyBoardUtils;
import com.hi.utilslqr.UIUtils;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @描述 搜索用户（本地、网上）
 */
public class SearchUserActivity extends BaseActivity {

    private NimUserInfo mUser;
    public static final String SEARCH_TYPE = "search_type";
    public boolean isSearchUserLocal = SEARCH_USER_LOCAL;
    public static final boolean SEARCH_USER_LOCAL = false;
    public static final boolean SEARCH_USER_REMOTE = false;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.etSearch)
    EditText mEtSearch;

    @Bind(R.id.rlNoResultTip)
    RelativeLayout mRlNoResultTip;
    @Bind(R.id.llSearch)
    LinearLayout mLlSearch;
    @Bind(R.id.tvMsg)
    TextView mTvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initView();
        initListener();
    }

    public void init() {
        isSearchUserLocal = getIntent().getBooleanExtra(SEARCH_TYPE, SEARCH_USER_LOCAL);
    }

    public void initView() {
        setContentView(R.layout.activity_search_user);
        ButterKnife.bind(this);
        initToolbar();
    }

    public void initListener() {
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRlNoResultTip.setVisibility(View.GONE);
                if (TextUtils.isEmpty(mEtSearch.getText().toString().trim())) {
                    mLlSearch.setVisibility(View.GONE);
                } else {
                    mLlSearch.setVisibility(View.VISIBLE);
                    mTvMsg.setText(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //监听键盘回车或搜索
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (TextUtils.isEmpty(mEtSearch.getText().toString().trim())) {
                        KeyBoardUtils.closeKeybord(mEtSearch, SearchUserActivity.this);
                    } else {
                        doSearch();
                    }
                    return true;
                }
                return false;
            }
        });

        mLlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch();
            }
        });
    }

    private void doSearch() {
        showWaitingDialog("请稍等");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String account = mEtSearch.getText().toString().trim();
        if (isSearchUserLocal) {
            mUser = NimUserInfoSDK.getUser(account);
            searchDone();
            hideWaitingDialog();
        } else {
            NimUserInfoSDK.getUserInfoFromServer(account, new RequestCallback<List<NimUserInfo>>() {
                @Override
                public void onSuccess(List<NimUserInfo> param) {
                    if (param != null && param.size() > 0) {
                        mUser = param.get(0);
                        T.showShort(SearchUserActivity.this,"搜索成功！");
                        searchDone();
                    }
                    hideWaitingDialog();
                }

                @Override
                public void onFailed(int code) {
                    UIUtils.showToast("搜索失败" + code);
                    hideWaitingDialog();
                }

                @Override
                public void onException(Throwable exception) {
                    exception.printStackTrace();
                    hideWaitingDialog();
                }
            });
        }
    }

    private void searchDone() {
        if (mUser == null) {
            mRlNoResultTip.setVisibility(View.VISIBLE);
        } else {
            mRlNoResultTip.setVisibility(View.GONE);
            //跳转到用户信息界面
            Intent intent = new Intent(this, UserInfoActivity.class);
            intent.putExtra("account", mUser.getAccount());
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
        mEtSearch.setVisibility(View.VISIBLE);
        mEtSearch.setHintTextColor(UIUtils.getColor(R.color.gray2));
        mEtSearch.setTextColor(UIUtils.getColor(R.color.white));
    }

}
