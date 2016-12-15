package com.fangyuan.weipanbao.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.fangyuan.weipanbao.R;
import com.fangyuan.weipanbao.adapter.ArrayWheelAdapter;

import com.fangyuan.weipanbao.model.AxisModel;
import com.fangyuan.weipanbao.model.StripLineChartModel;
import com.fangyuan.weipanbao.view.LineChartStripSV;
import com.fangyuan.weipanbao.view.OnWheelScrollListener;
import com.fangyuan.weipanbao.view.WheelView;

public class MainActivity extends Activity implements View.OnClickListener, OnWheelScrollListener {

    private View llMore;
    private PopupWindow popupWindow;
    private ArrayWheelAdapter arrWheelAdapter;
    private View viewAnchor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        llMore = findViewById(R.id.ll_more);
        llMore.setOnClickListener(this);
        viewAnchor = findViewById(R.id.view_anchor);
        WheelView wheelView = (WheelView) findViewById(R.id.wheelView);

        Integer[] arr = {100, 200, 300, 400, 500};
        arrWheelAdapter = new ArrayWheelAdapter(this, arr);
        wheelView.setViewAdapter(arrWheelAdapter);
        wheelView.addScrollingListener(this);

        LineChartStripSV svChart = (LineChartStripSV) findViewById(R.id.sv_chart);

        AxisModel axisX=new AxisModel(10);
        AxisModel axisY=new AxisModel(10);
        StripLineChartModel chartModel=new StripLineChartModel(10,10,10,10,axisX,axisY);
        svChart.setData(chartModel);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_more:
                showPopMenu();
                break;
            case R.id.ll_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_personal_center:
                Intent intent1=new Intent(this,PersonalCenterActivity.class);
                startActivity(intent1);
                break;
        }
    }

    private void showPopMenu() {
        if (null == popupWindow) {
            LinearLayout llMenu = (LinearLayout) getLayoutInflater().inflate(R.layout.popwindow_more, null);
            View llBuyTogetherHall = llMenu.findViewById(R.id.ll_buy_together_hall);
            llMenu.findViewById(R.id.ll_settings).setOnClickListener(this);
            llMenu.findViewById(R.id.ll_personal_center).setOnClickListener(this);
            popupWindow = new PopupWindow(llMenu, 500, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setAnimationStyle(R.style.popwindow_anim);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setOutsideTouchable(true);
        }
        int[] outLocation=new int[2];
        llMore.getLocationOnScreen(outLocation);
        popupWindow.showAtLocation(viewAnchor, Gravity.NO_GRAVITY, 0, outLocation[1]);
    }


    @Override
    public void onScrollingStarted(WheelView wheel) {

    }

    @Override
    public void onScrollingFinished(WheelView wheel) {
        int currentIndex = wheel.getCurrentItem();
        CharSequence itemValue = arrWheelAdapter.getItemText(currentIndex);
        Toast.makeText(this, "selected " + itemValue, Toast.LENGTH_SHORT).show();
    }
}
