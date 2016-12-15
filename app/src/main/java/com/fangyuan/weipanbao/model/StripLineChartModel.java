package com.fangyuan.weipanbao.model;

import java.util.List;

/**
 * Created by Administrator on 2016/12/14 0014.
 */

public class StripLineChartModel {
   public AxisModel axisX;
  public   AxisModel axisY;
   public List<PriceModel> priceModelList;

   public int leftPadding;
   public int bottomPadding;
    public int topPadding;
    public int rightPadding;

    public StripLineChartModel(int leftPadding,int rightPadding,int bottomPadding,int topPadding,AxisModel axisX,AxisModel axisY){
        this.leftPadding=leftPadding;
        this.rightPadding=rightPadding;
        this.bottomPadding=bottomPadding;
        this.topPadding=topPadding;

        this.axisX=axisX;
        this.axisY=axisY;
    }
}
