package com.fangyuan.weipanbao.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;

import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.util.Base64;

import com.fangyuan.weipanbao.R;
import com.fangyuan.weipanbao.model.PicBase64;
import com.fangyuan.weipanbao.model.RespObj;
import com.fangyuan.weipanbao.model.picBaseDetail;
import com.fangyuan.weipanbao.protocol.HttpReqManager;
import com.fangyuan.weipanbao.protocol.SimpleSubscriber;
import com.yolanda.nohttp.rest.Response;



import java.util.Arrays;
import java.util.List;


/**
 * Created by Administrator on 2016/12/22.
 */

public class RegisterActivity extends Activity implements View.OnClickListener, TextWatcher {

    private EditText etPhoneNum;
    private ImageView ivRequestPicVcode;
    private int regId=0;

    /*移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188

            　　联通：130、131、132、152、155、156、185、186

            　　电信：133、153、180、189、（1349卫通）*/
    String[] phoneNumPrefixArr={"134","135","136","137","138","139","150","151","152","157","158","159","182","183","184","187","188","147","178",
                "130","131","132","152","155","156","185","186","145","176","185",
                    "133","153","180","181","189","177"};
    List<String> phoneNumPrefixList;
    private EditText etPicVcodeAnswer;
    private static final int TAG_REQUEST_PIC_VCODE=1000;
    private static final int TAG_REQUEST_SMS_VCODE=2000;
    private static final int TAG_REQUEST_REGISTER=3000;
    private TextView tvRegister;
    private EditText etSmsVCode;

    private String smsVCodeStr;
    private String phoneNumStr;
    private String picVCodeStr;

    private EditText etInputPwd;
    private EditText etConfirmPwd;
    private String inputtedPwdStr;
    private String confirmedPwdStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initData();
    }

    private void initData() {
      phoneNumPrefixList=   Arrays.asList(phoneNumPrefixArr);
        requestPicVCode();
    }

    private void initView() {
        etPicVcodeAnswer = (EditText) findViewById(R.id.et_pic_vcode_answer);
        etPhoneNum = (EditText) findViewById(R.id.et_phone_num);
        etSmsVCode = (EditText) findViewById(R.id.et_sms_verify_code);
        etInputPwd = (EditText) findViewById(R.id.et_input_pwd);
        etConfirmPwd = (EditText) findViewById(R.id.et_confirm_pwd);

        etPicVcodeAnswer.addTextChangedListener(this);
        etPhoneNum.addTextChangedListener(this);
        etSmsVCode.addTextChangedListener(this);
        etInputPwd.addTextChangedListener(this);
        etConfirmPwd.addTextChangedListener(this);

    findViewById(R.id.tv_request_sms_vcode).setOnClickListener(this);
        findViewById(R.id.iv_register_back).setOnClickListener(this);
        ivRequestPicVcode = (ImageView) findViewById(R.id.iv_request_pic_vcode);
        ivRequestPicVcode.setOnClickListener(this);
        tvRegister = (TextView) findViewById(R.id.tv_register);
        tvRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_request_sms_vcode:
                requestSmsVerifyCode();
                break;
            case R.id.iv_request_pic_vcode:
               requestPicVCode();
                break;
            case R.id.tv_register:
                register();
                break;
            case R.id.iv_register_back:
                finish();
                break;
        }
    }

    private void register() {
        checkPhoneNum();
        checkPicVCode();
        checkSmsVCode();
        checkInputPassword();
        checkConfirmedPassword();
        //Log.i("info2","to register-------------");
        HttpReqManager httpReqManager=new HttpReqManager();
        httpReqManager.requestRegister(TAG_REQUEST_REGISTER,regId,phoneNumStr,picVCodeStr,smsVCodeStr,inputtedPwdStr,confirmedPwdStr,new MySimpSubscriber());
    }
private void checkPicVCode(){
    //String picVcodeAnswerStr= etPicVcodeAnswer.getText().toString();
    //isPicVCodeValid=false;
    if(TextUtils.isEmpty(picVCodeStr)){
        Toast.makeText(this,"图形验证码为空",Toast.LENGTH_SHORT).show();
        return;
    }
    //isPicVCodeValid=true;
}
    private void checkSmsVCode(){
        if(TextUtils.isEmpty(smsVCodeStr)){
            Toast.makeText(this,"短信验证码为空",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * 6-16位 数字和字母组合，或纯数字，或纯字母
     */
    private void checkInputPassword(){
        String regexStr="^[0-9a-zA-Z]{6,16}$";
       boolean ifValid= inputtedPwdStr.matches(regexStr);
        if(!ifValid){
            Toast.makeText(this,"输入的密码格式非法",Toast.LENGTH_SHORT).show();
                    return;
        }
    }
    private void checkConfirmedPassword(){
        String regexStr="^[0-9a-zA-Z]{6,16}$";
        boolean ifValid=confirmedPwdStr.matches(regexStr);
        if(!ifValid){
            Toast.makeText(this,"确认的密码格式非法",Toast.LENGTH_SHORT).show();
            return ;
        }
        //boolean isSame=false;
        if(!TextUtils.isEmpty(confirmedPwdStr)){
            if(confirmedPwdStr.equals(inputtedPwdStr)){
               // isSame=true;
            }else{
                Toast.makeText(this,"确认的密码和上次的密码不一致",Toast.LENGTH_SHORT).show();
                return ;
            }
        }else{
            Toast.makeText(this,"确认的密码为空",Toast.LENGTH_SHORT).show();
            return ;
        }

    }
    private void checkPhoneNum(){
         //phoneNumStr=   etPhoneNum.getText().toString();

        if(TextUtils.isEmpty(phoneNumStr)){
            Toast.makeText(this,"手机号不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        //判断手机号是否合法
        if(phoneNumStr.length()!=11){
            Toast.makeText(this,"手机号长度不对",Toast.LENGTH_SHORT).show();
            return;
        }
        String prefix=    phoneNumStr.substring(0,3);
        Log.i("info2","phoneNum prefix="+prefix);
        if(!phoneNumPrefixList.contains(prefix)){
            Toast.makeText(this,"手机号格式非法",Toast.LENGTH_SHORT).show();
            return;
        }

        }
    private void requestPicVCode(){
    HttpReqManager httpReqManager=new HttpReqManager();
    httpReqManager.requestPicVerifyCode(TAG_REQUEST_PIC_VCODE,regId,new MySimpSubscriber());
}
    private void requestSmsVerifyCode() {
     String phoneNum=   etPhoneNum.getText().toString();
    if(TextUtils.isEmpty(phoneNum)){
        Toast.makeText(this,"手机号不能为空",Toast.LENGTH_SHORT).show();
        return;
    }
        //判断手机号是否合法
        if(phoneNum.length()!=11){
            Toast.makeText(this,"手机号长度不对",Toast.LENGTH_SHORT).show();
            return;
        }
    String prefix=    phoneNum.substring(0,3);
        Log.i("info2","phoneNum prefix="+prefix);
    if(!phoneNumPrefixList.contains(prefix)){
            Toast.makeText(this,"手机号格式非法",Toast.LENGTH_SHORT).show();
            return;
    }
        //
       String picVcodeAnswerStr= etPicVcodeAnswer.getText().toString();
        if(TextUtils.isEmpty(picVcodeAnswerStr)){
            Toast.makeText(this,"图形验证码为空",Toast.LENGTH_SHORT).show();
            return;
        }
        HttpReqManager httpReqManager=new HttpReqManager();
        httpReqManager.requestSmsVerifyCode(TAG_REQUEST_SMS_VCODE,regId, phoneNum, picVcodeAnswerStr,new MySimpSubscriber());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(etSmsVCode.isFocused()){
            smsVCodeStr = etSmsVCode.getText().toString();
            //checkSmsVCode();
        }else if(etPhoneNum.isFocused()){
            //checkPhoneNum();
            phoneNumStr = etPhoneNum.getText().toString();
        }else if(etPicVcodeAnswer.isFocused()){
            //checkPicVCode();
            picVCodeStr = etPicVcodeAnswer.getText().toString();
        }else if(etInputPwd.isFocused()){
            inputtedPwdStr = etInputPwd.getText().toString();
        }else if(etConfirmPwd.isFocused()){
            confirmedPwdStr = etConfirmPwd.getText().toString();
        }
    if(!TextUtils.isEmpty(smsVCodeStr)&&!TextUtils.isEmpty(phoneNumStr)
            &&!TextUtils.isEmpty(picVCodeStr)&&!TextUtils.isEmpty(inputtedPwdStr)
            &&!TextUtils.isEmpty(confirmedPwdStr)){
        tvRegister.setBackgroundResource(R.drawable.shape_round_corner_rectangle_solid_blue);
        tvRegister.setClickable(true);
    }else{
        tvRegister.setBackgroundResource(R.drawable.shape_round_corner_rectangle_solid_gray);
        tvRegister.setClickable(false);
    }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
    class MySimpSubscriber extends SimpleSubscriber<Response<String>>{
        @Override
        public void onNext(Response<String> stringResponse) {
          int tag= (int) stringResponse.getTag();
          switch (tag){
              case TAG_REQUEST_PIC_VCODE:
                  String picStr=  stringResponse.get();
                  Log.i("info2","pic vcode resp="+picStr);
                  PicBase64 picBase64=   JSON.parseObject(picStr, PicBase64.class);
                  if(null!=picBase64&&null!=picBase64.strImgYzm&&!picBase64.strImgYzm.isEmpty()) {
                      picBaseDetail picBaseDetail = picBase64.strImgYzm.get(0);
                      regId = picBaseDetail.regId;
                      byte[] picArr = Base64.decodeFast(picBaseDetail.ImageYzm);
                      Bitmap bmp = BitmapFactory.decodeByteArray(picArr, 0, picArr.length);
                      ivRequestPicVcode.setImageBitmap(bmp);
                      //Log.i("info2", "response=" + picStr);
                  }
                  break;
              case TAG_REQUEST_SMS_VCODE:
               String smsStr=stringResponse.get();
                  Log.i("info2","sms resp="+smsStr);
               RespObj respObj=   JSON.parseObject(smsStr, RespObj.class);
                  if(null!=respObj&&null!=respObj.RspMsg&&!respObj.RspMsg.isEmpty()){
                      int runCode= respObj.RspMsg.get(0).runCode;
                      /*if(runCode==0){
                          Toast.makeText(RegisterActivity.this,"短信已经发送，请查收",Toast.LENGTH_SHORT).show();
                      }*/
                      switch (runCode){
                          case 0:
                              Toast.makeText(RegisterActivity.this,"短信已经发送，请查收",Toast.LENGTH_SHORT).show();
                              break;
                          case 110002:
                              Toast.makeText(RegisterActivity.this,"该手机号已经注册过",Toast.LENGTH_SHORT).show();
                              break;
                          case 110003:
                              Toast.makeText(RegisterActivity.this,"图片验证码错误",Toast.LENGTH_SHORT).show();
                              break;
                      }
                  }
                  break;
                case TAG_REQUEST_REGISTER:
                    String respStr=  stringResponse.get();
               Log.i("info2","register resp="+respStr);
                    RespObj respObj2=   JSON.parseObject(respStr, RespObj.class);
                    if(null!=respObj2&&null!=respObj2.RspMsg&&!respObj2.RspMsg.isEmpty()){
                     int runCode=   respObj2.RspMsg.get(0).runCode;
                        switch (runCode){
                            case 0:
                                Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                                break;
                            case 100545:
                                Toast.makeText(RegisterActivity.this,"上传的字符串长度不符合要求",Toast.LENGTH_LONG).show();
                                break;
                            case 110005:
                                Toast.makeText(RegisterActivity.this,"短信验证码错误",Toast.LENGTH_LONG).show();
                                break;
                            case 110003:
                                Toast.makeText(RegisterActivity.this,"图片验证码错误",Toast.LENGTH_LONG).show();
                                break;

                        }
                    }
                  break;


          }


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
