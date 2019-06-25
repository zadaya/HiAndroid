package com.hi.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hi.R;
import com.hi.activity.LocationActivity;
import com.hi.activity.NearbyMapActivity;
import com.hi.activity.SessionActivity;
import com.hi.activity.WelcomeActivity;
import com.hi.widget.CustomDialog;
import com.lzy.imagepicker.ui.ImageGridActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.hi.activity.SessionActivity.IMAGE_PICKER;
import static com.hi.activity.SessionActivity.REQUEST_CODE;

/**
 * @描述 聊天界面功能页面1
 */
public class Func1Fragment extends BaseFragment {

    private View mContentView;
    private CustomDialog mDialog;
    private TextView mTvOne;
    private TextView mTvTwo;


    @Bind(R.id.llPic)
    LinearLayout mLlPic;
    @Bind(R.id.llRecord)
    LinearLayout mLlRecord;
    @Bind(R.id.llRedPacket)
    LinearLayout mLlRedPacket;
    @Bind(R.id.llTransfer)
    LinearLayout mLlTransfer;

    @Bind(R.id.llCollection)
    LinearLayout mLlCollection;
    @Bind(R.id.llLocation)
    LinearLayout mLlLocation;
    @Bind(R.id.llVideo)
    LinearLayout mLlVideo;
    @Bind(R.id.llBusinessCard)
    LinearLayout mLlBusinessCard;

    Intent mIntent;
    Intent lIntent;

    @OnClick({R.id.llPic, R.id.llRecord, R.id.llRedPacket, R.id.llTransfer, R.id.llLocation, R.id.llVideo})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.llPic:
                mIntent = new Intent(getActivity(), ImageGridActivity.class);
                startActivityForResult(mIntent, IMAGE_PICKER);
                break;
            case R.id.llRecord:
                ((SessionActivity)getActivity()).showPlayVideo();
                break;
            case R.id.llLocation:
                lIntent = new Intent(getActivity(), LocationActivity.class);
                startActivityForResult(lIntent, REQUEST_CODE);
                break;
            case R.id.llVideo:
                mContentView = View.inflate(getActivity(), R.layout.dialog_menu_two_session, null);
                mDialog = new CustomDialog(getActivity(), mContentView, R.style.dialog);
                mDialog.show();
                mTvOne = (TextView) mContentView.findViewById(R.id.tvOne);
                mTvTwo = (TextView) mContentView.findViewById(R.id.tvTwo);
                mTvOne.setText("视频聊天");
                mTvTwo.setText("语音聊天");
                mTvOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                mTvTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                break;

        }
    }


    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.frag_func_page1, null);
        ButterKnife.bind(this, view);
        return view;
    }

}
