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



    /**
     * axis length,view width-padding left-padding right or
     * view height-padding top-padding bottom
     */
   public float axisLength;



    /**
     * 轴分为多少份
     */
    public int keduCount=10;



    /**
     * 轴等分后，每一份长度（px）
     */
    public int kedu;



    /**
     * 几个刻度一组，够一组画个长的刻度
     */
    public int keduStride=3;


    public AxisModel(int keduCount){
        this.keduCount=keduCount;
    }


   /* public void setViewSide(int sideLength){
        axisLength=sideLength*0.8f;
        kedu=axisLength/keduCount;
        unit=axisLength/(end-start);
    }*/
    public void setAxisPxLength(int axisLength,float yMinValue,float yMaxValue){
        this.axisLength=axisLength;
        kedu=axisLength/keduCount;
        //unit=axisLength/(end-start);
        unit=axisLength/(yMaxValue-yMinValue);
    }

    /**
     * 调用顺序 setAxisPxLength---setAxisMaxMinValue
     * @param axisLength
     */
    public void setAxisPxLength(int axisLength){
        this.axisLength=axisLength;
        kedu=axisLength/keduCount;
        //unit=axisLength/(end-start);
        //unit=axisLength/(yMaxValue-yMinValue);
    }
    public void setAxisPxLength(int axisLength,int dataSize){
        this.axisLength=axisLength;
        kedu=axisLength/dataSize;
        //unit=axisLength/(end-start);
        //unit=axisLength/(yMaxValue-yMinValue);
    }
    /**
     * 此方法需要 setAxisPxLength（）传入 axisLen,计算才正确
     * @param maxValue
     * @param minValue
     */
    public void setAxisMaxMinValue(float maxValue,float minValue){
        unit=axisLength/(maxValue-minValue);
    }
    public void updateUnit(float start,float end){
        this.start=start;
        this.end=end;
        unit=axisLength/(end-start);
    }
    public float getAxisLength() {
        return axisLength;
    }
    public float getUnit() {
        return unit;
    }

    public int getKeduCount() {
        return keduCount;
    }

    public void setKeduCount(int keduCount) {
        this.keduCount = keduCount;
    }
    public int getKedu() {
        return kedu;
    }
    public int getKeduStride() {
        return keduStride;
    }

    public void setKeduStride(int keduStride) {
        this.keduStride = keduStride;
    }
}
