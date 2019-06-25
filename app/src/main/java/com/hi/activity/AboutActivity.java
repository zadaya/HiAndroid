package com.hi.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.hi.R;
import com.hi.utils.AlipaySdk;
import com.hi.utils.T;
import com.hi.widget.Topbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 关于页面
 */

public class AboutActivity extends BaseActivity {


    @Bind(R.id.topbar_about)
    Topbar topbarAbout;
//    @Bind(R.id.iv_pay)
//    ImageView mIvPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        topbarAbout.setTopbarOnClickListener(new Topbar.TopbarOnClickListener() {
            @Override
            public void leftOnClick() {
                finish();
            }

            @Override
            public void rightOnClick() {

            }
        });
    }

//    @OnClick(R.id.iv_pay)
//    public void onViewClicked() {
//        if (AlipaySdk.hasInstalledAlipayClient(AboutActivity.this)) {
//            AlipaySdk.startAlipayClient(AboutActivity.this, "FKX03380N3ZXGG0WJUKL60");
//        } else {
//            T.showShort(AboutActivity.this,"谢谢，您没有安装支付宝客户端");
//        }
//    }
}
