package com.fangyuan.weipanbao.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.fangyuan.weipanbao.R;
import com.fangyuan.weipanbao.adapter.ArrayWheelAdapter;

import com.fangyuan.weipanbao.model.AxisModel;
import com.fangyuan.weipanbao.model.PriceModel;
import com.fangyuan.weipanbao.model.StripLineChartModel;
import com.fangyuan.weipanbao.util.HttpPostUtil;
import com.fangyuan.weipanbao.view.LineChartStripSV2;
import com.fangyuan.weipanbao.view.OnWheelScrollListener;
import com.fangyuan.weipanbao.view.WheelView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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

        LineChartStripSV2 svChart = (LineChartStripSV2) findViewById(R.id.sv_chart);

        AxisModel axisX=new AxisModel(10);
        AxisModel axisY=new AxisModel(10);

        List<PriceModel> priceModelList=new ArrayList<>();


        for (int i = 0; i <70 ; i++) {
            float fiveDayAvgPrice = get5DayAvgPrice();
            PriceModel priceModel=new PriceModel(getTopPrice(),getBottomPrice(),getOpenClosePrice(),getOpenClosePrice(),fiveDayAvgPrice,""+i);//"12月"+i+"日"
            priceModel.setRectCoordOffset(this,5);
            priceModelList.add(priceModel);
            Log.i("info2","time="+i+"-5dayAvg="+fiveDayAvgPrice);
        }
            //bottomPadding 10-->50
        StripLineChartModel chartModel=new StripLineChartModel(10,10,50,10,axisX,axisY,priceModelList);
        svChart.setData(chartModel);

       //new HttpPostUtil().httpUrlConnection();
    }

    private float getOpenClosePrice(){
        //40.000-80.000
        //4-8
        //open close 3.630-3.666
        //bottom 3.600-3.630/      avg 3.600-3.699/    /top 3.666-3.699
        Random random=new Random();
        float randomValue = random.nextFloat() * 0.036f+3.630f;
       float randomValueFormat=  (float)(Math.round(randomValue*1000))/1000;
    Log.i("info2","random value="+randomValueFormat);
    return randomValueFormat;
    }
    private float get5DayAvgPrice(){
        //20.000-100.000
        //open close 3.630-3.666
        //bottom 3.600-3.630/      avg 3.600-3.699/    /top 3.666-3.699
        Random random=new Random();
        float randomValue = random.nextFloat() * 0.099f+3.600f;
        float randomValueFormat=  (float)(Math.round(randomValue*1000))/1000;
        Log.i("info2","random value="+randomValueFormat);
        return randomValueFormat;
    }
    private float getTopPrice(){
        //80--100
        //3.666-3.699/
        // 0-1

        Random random=new Random();
        float randomValue = random.nextFloat() * 0.033f+3.666f;
        float randomValueFormat=  (float)(Math.round(randomValue*1000))/1000;
        Log.i("info2","random value="+randomValueFormat);
        return randomValueFormat;
    }
    private float getBottomPrice(){
        //20--40
        //2-4
        //open close 3.630-3.666
        //bottom 3.600-3.630/      avg 3.600-3.699/    /top 3.666-3.699
        Random random=new Random();
        float randomValue = random.nextFloat() * 0.030f+3.600f;
        //keep 3 fraction 保留三位小数
        float randomValueFormat=  (float)(Math.round(randomValue*1000))/1000;
        Log.i("info2","random value="+randomValueFormat);
        return randomValueFormat;
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
