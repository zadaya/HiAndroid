package com.hi.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hi.R;
import com.hi.nimsdk.NimUserInfoSDK;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * @描述 修改签名
 */
public class ChangeNameActivity extends BaseActivity {

    private String mName;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.btnOk)
    Button mBtnOk;
    @Bind(R.id.etName)
    EditText mEtName;

    @OnClick({R.id.btnOk})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btnOk:
                String name = mEtName.getText().toString();
                if (TextUtils.isEmpty(name.trim())) {
                    showMaterialDialog("提示", "没有输入昵称，请重新填写", "确定", "", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideMaterialDialog();
                        }
                    }, null);
                } else {
                    showWaitingDialog("请稍等");
                    Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
                    fields.put(UserInfoFieldEnum.Name, name);
                    NimUserInfoSDK.updateUserInfo(fields, new RequestCallbackWrapper<Void>() {
                        @Override
                        public void onResult(int code, Void result, Throwable exception) {
                            hideWaitingDialog();
                            finish();
                        }
                    });
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initView();
    }

    public void init() {
        mName = getIntent().getStringExtra("name");
    }

    public void initView() {
        setContentView(R.layout.activity_change_name);
        ButterKnife.bind(this);
        initToolbar();
        mEtName.setText(mName);
        mEtName.setSelection(mName.length());
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("修改签名");
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
        mBtnOk.setVisibility(View.VISIBLE);
    }

}
