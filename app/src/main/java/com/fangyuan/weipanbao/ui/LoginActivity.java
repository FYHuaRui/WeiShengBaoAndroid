package com.fangyuan.weipanbao.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;


import com.fangyuan.weipanbao.R;

/**
 * Created by Administrator on 2016/12/26.
 */

public class LoginActivity extends Activity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        RadioGroup radioGroup= (RadioGroup) findViewById(R.id.rg_login_way);
        radioGroup.setOnCheckedChangeListener(this);
        radioGroup.check(R.id.rb_accout_pwd_login);

        findViewById(R.id.iv_back).setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
         case R.id.rb_accout_pwd_login:
            //startFragment
          Fragment accountPwdLoginFragment=  AccountPwdLoginFragment.newInstance("","");
           getFragmentManager().beginTransaction().replace(R.id.ll_fragment_container,accountPwdLoginFragment).commit();
            break;
            case  R.id.rb_sms_vcode_login:
                Fragment smsVCodeLoginFragment= SmsVCodeLoginFragment.newInstance("","");
                getFragmentManager().beginTransaction().replace(R.id.ll_fragment_container,smsVCodeLoginFragment).commit();
            break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
