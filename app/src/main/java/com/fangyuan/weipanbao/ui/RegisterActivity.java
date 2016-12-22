package com.fangyuan.weipanbao.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.Base64;
import com.fangyuan.weipanbao.R;
import com.fangyuan.weipanbao.model.PicBase64;
import com.fangyuan.weipanbao.protocol.HttpReqManager;
import com.fangyuan.weipanbao.protocol.SimpleSubscriber;
import com.yolanda.nohttp.rest.Response;

/**
 * Created by Administrator on 2016/12/22.
 */

public class RegisterActivity extends Activity implements View.OnClickListener {

    private EditText etPhoneNum;
    private ImageView ivRequestPicVcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        etPhoneNum = (EditText) findViewById(R.id.et_phone_num);
    findViewById(R.id.tv_request_verify_code).setOnClickListener(this);
        ivRequestPicVcode = (ImageView) findViewById(R.id.iv_request_pic_vcode);
        ivRequestPicVcode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_request_verify_code:
                requestVerifyCode();
                break;
            case R.id.iv_request_pic_vcode:
                HttpReqManager httpReqManager=new HttpReqManager(this);
                httpReqManager.requestPicVerifyCode(0,new MySimpSubscriber());
                break;
        }
    }

    private void requestVerifyCode() {
     String phoneNum=   etPhoneNum.getText().toString();
    if(TextUtils.isEmpty(phoneNum)){
        Toast.makeText(this,"手机号不能为空",Toast.LENGTH_SHORT).show();
        return;
    }
        //判断手机号是否合法
        //if()

    }
    class MySimpSubscriber extends SimpleSubscriber<Response<String>>{

        @Override
        public void onNext(Response<String> stringResponse) {
          String picStr=  stringResponse.get();
         PicBase64 picBase64=   JSON.parseObject(picStr, PicBase64.class);
      byte[] picArr=   Base64.decodeFast(picBase64.strImgYzm.get(0).ImageYzm);
         Bitmap bmp=   BitmapFactory.decodeByteArray(picArr,0,picArr.length);
           ivRequestPicVcode.setImageBitmap(bmp);
            Log.i("info2","response="+picStr);
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            Log.i("info2",e.getLocalizedMessage());
        }
    }
}
