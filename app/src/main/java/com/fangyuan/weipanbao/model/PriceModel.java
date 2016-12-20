package com.fangyuan.weipanbao.model;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/12/14 0014.
 */

public class PriceModel {
    private float topPrice;
    private float bottomPrice;
    private float minPrice;

    public float getFiveDayAvgPrice() {
        return fiveDayAvgPrice;
    }

    public float getTopPrice() {
        return topPrice;
    }

    public float getBottomPrice() {
        return bottomPrice;
    }

    public float getOpenPrice() {
        return openPrice;
    }

    public float getClosePrice() {
        return closePrice;
    }

    private float openPrice;
    private float closePrice;

    private float fiveDayAvgPrice;



    public String time;
    //
    private String timeFormat;
    /**
     * 以上所有价格的最大值
     */
    private float maxPrice;

    List<Float> allPriceList=new ArrayList<Float>();



    public int color;
    public float rectCoordOffset=0;
    public PriceModel(float topPrice, float bottomPrice, float openPrice, float closePrice, float fiveDayAvgPrice, String time) {
        this.topPrice = topPrice;
        this.bottomPrice = bottomPrice;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.fiveDayAvgPrice = fiveDayAvgPrice;
        this.time = time;



        if(openPrice>closePrice){
            color= Color.GREEN;
        }else{
            color=Color.RED;
        }

        allPriceList.add(topPrice);
        allPriceList.add(bottomPrice);
        allPriceList.add(openPrice);
        allPriceList.add(closePrice);
        allPriceList.add(fiveDayAvgPrice);

        maxPrice = Collections.max(allPriceList);
        minPrice=Collections.min(allPriceList);
        Log.i("info2","topPrice="+topPrice+"-bottomPrice="+bottomPrice+"-openPrice="+openPrice+"-closePrice="+closePrice+"-avgPrice="+fiveDayAvgPrice);
        Log.i("info2","max Price="+maxPrice);
    }
    public void setRectCoordOffset(Context context, int offsetDp){
        DisplayMetrics dm=context.getResources().getDisplayMetrics();
     rectCoordOffset=   TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,offsetDp,dm);
    }
    public float getMaxPrice() {
        return maxPrice;
    }
    public float getMinPrice(){
        return minPrice;
    }
    public String getTime() {
        return time;
    }
    public int getColor() {
        return color;
    }
}
