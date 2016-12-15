package com.fangyuan.weipanbao.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.fangyuan.weipanbao.R;

/**
 * Created by Administrator on 2016/12/14 0014.
 */

public class JointPurchaseHallActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint_purchase_hall);
        initData();
        initView();
    }

    private void initView() {
        ListView lvJointPurchase = (ListView) findViewById(R.id.lv_joint_purchase_list);

    }

    /**
     * start network request for joint purchase list,
     * in callback method,update UI
     */
    private void initData() {
    }
}
