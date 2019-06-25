package com.hi.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hi.R;
import com.hi.app.App;
import com.hi.app.AppConst;
import com.hi.model.ResponseData;
import com.hi.model.UserServer;
import com.hi.nimsdk.NimUserInfoSDK;
import com.hi.parse.JsonCallback;
import com.hi.utils.T;
import com.hi.widget.Topbar;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.OkGo;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.hi.activity.SessionActivity.IMAGE_PICKER;

/**
 * 注册的第二个页面
 */

public class RegisterNextActivity extends BaseActivity {

    @Bind(R.id.regist_topbar)
    Topbar registTopbar;
    @Bind(R.id.tv_accid)
    TextView tvAccid;
    @Bind(R.id.et_accid)
    EditText etAccid;
    @Bind(R.id.iv_select_photo)
    ImageView ivPhoto;
    @Bind(R.id.et_usernick)
    EditText etUsernick;
    @Bind(R.id.tv_usernick)
    TextView tvUsernick;
    @Bind(R.id.btn_register)
    Button btnRegister;

    private String tel;
    private String password;
    private File imgFile ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_regist_next);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tel = this.getIntent().getStringExtra("phone");
        password = this.getIntent().getStringExtra("password");
        if (tel == null || password == null) {
            finish();
            return;
        }

        registTopbar.setTopbarOnClickListener(new Topbar.TopbarOnClickListener() {
            @Override
            public void leftOnClick() {
                finish();
            }

            @Override
            public void rightOnClick() {
            }
        });
    }

    @OnClick({R.id.iv_select_photo, R.id.btn_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_select_photo:
                App.imagePicker.setMultiMode(false);
                Intent mIntent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(mIntent, IMAGE_PICKER);
                break;
            case R.id.btn_register:
                registerInServer();
                break;
        }
    }

    private void registerInServer() {
        // 先进行设置项的判断，然后进行上传
        String accid = etAccid.getText().toString().trim();
        String nickname = etUsernick.getText().toString().trim();
        if (TextUtils.isEmpty(accid)) {
            Toast.makeText(getApplicationContext(), "请填写账号", Toast.LENGTH_SHORT).show();
            etAccid.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(getApplicationContext(), "请设置昵称", Toast.LENGTH_SHORT).show();
            etUsernick.requestFocus();
            return;
        }

        if (imgFile == null || !imgFile.exists()) {
            T.showShort(RegisterNextActivity.this, "请上传您的头像");
            return;
        }
        showWaitingDialog("正在注册...");

        OkGo.post(AppConst.User.REGISTER)
                .tag(this)
                .params("tel", "")
                .params("username", etUsernick.getText().toString())
                .params("accid", etAccid.getText().toString())
                .params("password", password)
                .params("birthday", "")
                .params("sex", "")
                .params("province", "")
                .params("city", "")
                .params("sign", "")
                .params("userphoto", imgFile) //用户头像
                .params("homeid", 1)
                .execute(new JsonCallback<ResponseData<UserServer>>() {
                    @Override
                    public void onSuccess(ResponseData<UserServer> responseData, Call call, Response response) {

                        if (responseData.code == 300) {
                            T.showShort(RegisterNextActivity.this, "此账号号已经被注册，请更换一个");
                            hideWaitingDialog();
                            return;
                        }
                        if (responseData.isSuccess()) {
                            T.showShort(RegisterNextActivity.this, "注册成功");
                            Intent intent = new Intent(RegisterNextActivity.this, LoginActivity.class);
                            intent.putExtra("accid", responseData.getData().getAccid());
                            startActivity(intent);
                            finish();
                        } else {
                            T.showShort(RegisterNextActivity.this, "注册失败，可能用户名已经被注册");
                        }
                        hideWaitingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        T.showShort(RegisterNextActivity.this, "失败" );
                        hideWaitingDialog();
                    }

                });
        if (imgFile != null) {
            NimUserInfoSDK.uploadFile(imgFile, "image/jpeg", new RequestCallbackWrapper<String>() {
                @Override
                public void onResult(int code, String url, Throwable exception) {

                    if (code == ResponseCode.RES_SUCCESS && !TextUtils.isEmpty(url)) {// 上传成功得到Url
                        Map<UserInfoFieldEnum, Object> fields = new HashMap<UserInfoFieldEnum, Object>(1);
                        fields.put(UserInfoFieldEnum.AVATAR, url);
                    }

                    Map<UserInfoFieldEnum, Object> fields = new HashMap(1);
                    fields.put(UserInfoFieldEnum.AVATAR, url);
                    NimUserInfoSDK.updateUserInfo(fields, new RequestCallbackWrapper<Void>() {
                        @Override
                        public void onResult(int code, Void result, Throwable exception) {
                            if (code == ResponseCode.RES_SUCCESS) {// 上传成功
                                Log.d("uploadicon","上传成功");
                            } else {// 上传失败
                                Log.d("uploadicon","上传失败");
                            }
                            hideWaitingDialog();
                        }
                    });
                }
            });

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {//返回多张照片
            if (data != null) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>)
                        data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null && images.size() > 0) {
                    imgFile = new File(images.get(0).path);  //取第一张照片
                    Bitmap bt = BitmapFactory.decodeFile(imgFile.getAbsolutePath());//图片地址
                    ivPhoto.setImageBitmap(bt);
                }

            }
        }

    }

}
