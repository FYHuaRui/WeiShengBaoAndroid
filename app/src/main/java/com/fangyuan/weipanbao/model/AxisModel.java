package com.fangyuan.weipanbao.model;

/**
 * Created by Administrator on 2016/12/14 0014.
 */

public class AxisModel {
    //axis pixel height permanent;
    //axis data start,end diff;
    //unit=pixel height/data diff;
  public   float start;
   public float end;
   private float unit;
   public float axisLength;
    public int keduCount=10;

    public float kedu;
    int startX;
    int startY;
    int endX;
    int endY;

    public AxisModel(int keduCount){
        this.keduCount=keduCount;
    }

    /**
     * base view side(width or height) get axis length
     * @param sideLength
     */
    public void setViewSide(int sideLength){
        axisLength=sideLength*0.8f;
        kedu=axisLength/keduCount;
        unit=axisLength/(end-start);
    }
    public void updateUnit(float start,float end){
        this.start=start;
        this.end=end;
        unit=axisLength/(end-start);
    }
}
