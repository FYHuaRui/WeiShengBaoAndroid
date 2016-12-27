package com.fangyuan.weipanbao.protocol;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.yolanda.nohttp.NoHttp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;


/**
 * Created by Administrator on 2016/12/16.
 */

public class HttpReqManager {

private static final String SERVER="http://192.168.100.101:8080/wsb/java/servlet/";
    //private static final String SERVER="http://192.168.1.185:8080/wsb/java/servlet/";
   // Context mContext;
    private String smsVerifyCodeInterface ="RegisterGetShortMsgYzm";
    private String picVerifyCodeInterface="RegisterGetImageYzm";
    private String registerInterface="RegisterLastStep";
    private String loginInterface="";
    private String Interface="";

    private String postKey="param";

    public HttpReqManager(){
        //mContext=context;
    }

    public void requestSmsVerifyCode(int tag,int regId,String phoneNum,String picVCode,SimpleSubscriber simpleSubscriber){
        Request<String> stringRequest = NoHttp.createStringRequest(SERVER+ smsVerifyCodeInterface, RequestMethod.POST);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("regId",regId);
        jsonObject.put("telephone",phoneNum);
        jsonObject.put("stringYzm",picVCode);
      String jsonStr=  jsonObject.toString();
        stringRequest.add(postKey,jsonStr);
        stringRequest.toString();
        stringRequest.setTag(tag);
        RxNoHttp.request( stringRequest, simpleSubscriber);

    }
public void requestRegister(int tag,int regId,String phoneNum,String picVCode,String smsVCode,String password,String confirmPwd,SimpleSubscriber simpleSubscriber){
    Request<String> stringRequest = NoHttp.createStringRequest(SERVER + registerInterface,RequestMethod.POST);
    //stringRequest.add("regId",regId);
   JSONObject jsonObject=new JSONObject();
    jsonObject.put("regId",regId);
    jsonObject.put("telephone",phoneNum);
    jsonObject.put("stringYzm",picVCode);
    jsonObject.put("shortMsgYzm",smsVCode);
    jsonObject.put("password",password);
    jsonObject.put("pwdConfirm",confirmPwd);
    stringRequest.add("param",jsonObject.toString());
    stringRequest.setTag(tag);
    RxNoHttp.request( stringRequest, simpleSubscriber);
}
    public void requestPicVerifyCode(int tag,int regId,SimpleSubscriber simpleSubscriber){
        Request<String> stringRequest = NoHttp.createStringRequest(SERVER + picVerifyCodeInterface,RequestMethod.POST);
        //stringRequest.add("regId",regId);
        String jsonStr="{\"regId\" : "+regId+"}";
        stringRequest.add("param",jsonStr);
        stringRequest.setTag(tag);
        RxNoHttp.request( stringRequest, simpleSubscriber);
    }


}
