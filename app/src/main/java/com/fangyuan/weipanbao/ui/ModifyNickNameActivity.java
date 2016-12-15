package com.fangyuan.weipanbao.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fangyuan.weipanbao.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/12/13 0013.
 */

public class ModifyNickNameActivity extends Activity implements View.OnClickListener {

    private EditText etNickName;
    private String nickNameOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nick_name);
        initData();
        initView();
    }

    private void initData() {
        nickNameOriginal = getIntent().getStringExtra(SettingsActivity.KEY_NICK_NAME);
    }

    private void initView() {
        etNickName = (EditText) findViewById(R.id.et_modify_nickName);
        etNickName.setText(nickNameOriginal);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_confirm_modify).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.tv_confirm_modify:
                confirmModify();
                break;
        }
    }

    private void confirmModify() {
        String newNickName = etNickName.getText().toString();
        if(TextUtils.isEmpty(newNickName)){
            Toast.makeText(this,"昵称不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if(!validNickName(newNickName)){
            Toast.makeText(this,"昵称只能为英文、数字、下划线和汉字",Toast.LENGTH_LONG).show();
            return;
        }
        //upload new nickname--network opration
        //temp code 0
        Toast.makeText(this,"昵称修改成功",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent();

        intent.putExtra(SettingsActivity.KEY_NICK_NAME,newNickName);
        setResult(RESULT_OK,intent);
        finish();
        //test code 1
    }

    private boolean validNickName(String newNickName){
        Pattern pt= Pattern.compile("[\u4e00-\u9fa5\\w]+");
        Matcher mt=pt.matcher(newNickName);
        return mt.matches();
    }
}
