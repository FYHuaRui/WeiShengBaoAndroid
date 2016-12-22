package com.fangyuan.weipanbao.protocol;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

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

//private static final String SERVER="http://192.168.100.101:8080/wsb/java/servlet/";
    private static final String SERVER="http://192.168.100.105:8080/wsb/java/servlet/";
    Context mContext;
    public HttpReqManager(Context context){
        mContext=context;
    }
    private String smsVerifyCodeInterface ="";
    private String picVerifyCodeInterface="RegistGetImageYzm";
    private String registerInterface="";
    private String loginInterface="";
    private String Interface="";
    public void requestSmsVerifyCode(String phoneNum,SimpleSubscriber simpleSubscriber){
        Request<String> stringRequest = NoHttp.createStringRequest(SERVER+ smsVerifyCodeInterface, RequestMethod.POST);
        stringRequest.add("PhoneNum",phoneNum);
        RxNoHttp.request(mContext, stringRequest, simpleSubscriber);
    }

    public void requestPicVerifyCode(int regId,SimpleSubscriber simpleSubscriber){
        Request<String> stringRequest = NoHttp.createStringRequest(SERVER+ picVerifyCodeInterface,RequestMethod.POST);
        //stringRequest.add("regId",regId);
        String jsonStr="{\"regId\" : "+regId+"}";
        stringRequest.add("param",jsonStr);

        RxNoHttp.request(mContext, stringRequest, simpleSubscriber);
    }
    public  void httpUrlConnection(){
        new MyThread().start();
    }
    public  void httpUrlConnection2() {

        try {
            //String pathUrl = "http://192.168.10.189:8080/wsb/InterFace?InterName=yzm";
            //String pathUrl =   "http://192.168.100.101:8080/wsb/java/servlet/LoginSeleUser?param={test=505}";
            String pathUrl =   "http://192.168.100.101:8080/wsb/java/servlet/LoginSeleUser";
// 建立连接
            URL url = new URL(pathUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();


// //设置连接属性
            httpConn.setDoOutput(true);// 使用 URL 连接进行输出
            httpConn.setDoInput(true);// 使用 URL 连接进行输入
            httpConn.setUseCaches(false);// 忽略缓存
            httpConn.setRequestMethod("POST");// 设置URL请求方法
            String requestString = "客服端要以以流方式发送到服务端的数据...";

            //InterName=yzm
// 设置请求属性
// 获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致
            byte[] requestStringBytes = requestString.getBytes("UTF-8");
            httpConn.setRequestProperty("Content-length", "0");
            httpConn.setRequestProperty("Content-Type", "text/plain");
            httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            httpConn.setRequestProperty("Charset", "UTF-8");
//
            //String name = URLEncoder.encode("yzm", "utf-8");
            //httpConn.setRequestProperty("InterName", name);
            //httpConn.setRequestProperty("InterName", "yzm");
            httpConn.setRequestProperty("param","{test=505}");
            httpConn.connect();
// 建立输出流，并写入数据
            //OutputStream outputStream = httpConn.getOutputStream();
            //outputStream.write(requestStringBytes);
            //outputStream.close();
// 获得响应状态
            int responseCode = httpConn.getResponseCode();


            if (HttpURLConnection.HTTP_OK == responseCode) {// 连接成功
// 当正确响应时处理数据
                StringBuffer sb = new StringBuffer();
                String readLine;
                BufferedReader responseReader;
// 处理响应流，必须与服务器响应流输出的编码一致
                responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine).append("\n");
                }
                responseReader.close();
                //tv.setText(sb.toString());
                Log.i("info2","http post recv="+sb.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
    class MyThread extends Thread{
        @Override
        public void run() {
            httpUrlConnection2();
        }
    }
    private void httpPost(){
        /*String uriAPI = "http://127.0.0.1/xxx/xx.jsp";  //声明网址字符串
        HttpPost httpRequest = new HttpPost(uriAPI);   //建立HTTP POST联机
        List <NameValuePair> params = new ArrayList <NameValuePair>();   //Post运作传送变量必须用NameValuePair[]数组储存
        params.add(new BasicNameValuePair("str", "I am Post String"));
        httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));   //发出http请求
        HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);   //取得http响应
        if(httpResponse.getStatusLine().getStatusCode() == 200)
            String strResult = EntityUtils.toString(httpResponse.getEntity());*/   //获取字符串
    }
}
