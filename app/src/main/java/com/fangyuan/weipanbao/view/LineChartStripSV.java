package com.fangyuan.weipanbao.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.fangyuan.weipanbao.model.PriceModel;
import com.fangyuan.weipanbao.model.StripLineChartModel;
import com.fangyuan.weipanbao.util.MathUtil;

/**
 * Created by Administrator on 2016/12/13 0013.
 * <p>
 * support show many line,strip
 * content --》view width height
 * scale,move
 * <p>
 * 1,draw x y axis,kedu, data diff/px diff;
 * 2,draw line
 */

public class LineChartStripSV extends SurfaceView implements SurfaceHolder.Callback {


    //用于控制SurfaceView
    private SurfaceHolder sfh;
    private Handler handler = new Handler();
    private ImageRunnable imageRunnable = new ImageRunnable();
    private Paint axisPaint;
    private Canvas canvas;
    private Matrix matrix;

    /**
     * 图片的坐标
     */
    private float imageX, imageY;
    /**
     * 获取的图片
     */
    private Bitmap bmp;
    /**
     * 图片宽高
     */
    private float bmpW, bmpH;
    /**
     * 屏幕大小
     */
    private int screenW, screenH;
    private StripLineChartModel chartModel;
    private final Paint linePaint;
    private final Paint numberPaint;
    private final Paint pointPaint;

    /**
     * SurfaceView初始化函数
     */
    public LineChartStripSV(Context context, AttributeSet attrs) {
        super(context, attrs);
        sfh = this.getHolder();
        sfh.addCallback(this);
        axisPaint = new Paint();
        axisPaint.setColor(Color.GRAY);
        axisPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(Color.BLUE);

        numberPaint = new Paint();
        numberPaint.setColor(Color.WHITE);
        numberPaint.setTextSize(40);

        pointPaint = new Paint();
        pointPaint.setColor(Color.YELLOW);
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.STROKE);
        pointPaint.setStrokeWidth((float) 3);

        setFocusable(true);
    }

    /**
     * SurfaceView视图创建，响应此函数
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("ImageSurfaceView is surfaceCreated");
        screenH = this.getHeight();
        screenW = this.getWidth();
        handler.post(imageRunnable);
    }

    public void setData(StripLineChartModel chartModel) {
        this.chartModel = chartModel;
        // this.chartModel.axisY.
        invalidate();
    }

    /**
     * 绘图
     */
    public void draw() {
        try {
            canvas = sfh.lockCanvas();
            canvas.drawRGB(0, 0, 0);

            //根据宽高的比例来 画图表
            //excceed window,not draw

//1, draw axis--support scale( when chart scale)，axis margin
//chart scale,scale range,get chart max value,min value(visible)
//2,draw chart
//3,scroll horizontal
            //calculate point coordinate,axis kedu,redraw
//4,scale
            //calculate coordinate,redraw
//---------------------------------------------
            //1,draw x axis(刻度线 和x轴 垂直)--图表拖动 缩放，刻度线不会移动
            float startX = chartModel.leftPadding;
            float startY = screenH - chartModel.bottomPadding;

            float endX = screenW - chartModel.rightPadding;
            float endY = startY;

            //chartModel.axisY.setViewSide(screenH);
            int yAxisLen=screenH - chartModel.topPadding - chartModel.bottomPadding;
            chartModel.axisY.setAxisPxLength(yAxisLen);
            float kedu = chartModel.axisY.kedu;
            int keduCount = chartModel.axisY.keduCount;
            for (int i = 0; i < keduCount; i++) {
                canvas.drawLine(startX, startY - (i * kedu), endX, endY - (i * kedu), axisPaint);
            }
            //1.2 draw y kedu value
            float maxPrice = chartModel.getMaxPrice();
            float minPrice = chartModel.getMinPrice();
            //int maxPriceInt = (int) maxPrice;
            //float yMaxValue=maxPrice+maxPriceInt;
            //float yMinValue=minPrice-maxPriceInt;

            float yMaxValue = maxPrice;
            float yMinValue = minPrice;


            // y axis value range
            float valueDiff = yMaxValue - yMinValue;
            //y kedu  value,keduCount means y axis kedu count
            float valueUnit = valueDiff / keduCount;
            float axisLength = chartModel.axisY.getAxisLength();
            float pxUnit = axisLength / keduCount;
         float lenValueAspect= axisLength/valueDiff;
            for (int i = 0; i < keduCount; i++) {
                float startX2 = yMinValue + i * valueUnit;
                startX2 = MathUtil.get3FractionFloat(startX2);
                if (i != 0) {
                    canvas.drawText(startX2 + "", startX, startY - i * pxUnit, numberPaint);
                }
            }
            //2,draw y axis
            endY = chartModel.topPadding;
            //chartModel.axisX.setViewSide(screenW);
            int xAxisLen=screenW - chartModel.leftPadding - chartModel.rightPadding;
            chartModel.axisX.setAxisPxLength(xAxisLen);
            kedu = chartModel.axisX.kedu;
            keduCount = chartModel.axisX.keduCount;
            Log.i("info2", "axisX keduCount=" + keduCount);
            Log.i("info2", "axisX kedu=" + kedu);
//----------------------
            for (int i = 0; i < keduCount; i++) {
                canvas.drawLine(startX + (i * kedu), startY, startX + (i * kedu), endY, axisPaint);
            }
            //数据分布分为两种情况，
            //1,平铺，所有数据平均铺开，显示在屏幕上
            //2，顺序排列，从左至右顺序排列，不一定到达屏幕宽度
            //draw x kedu  value(12.1 12.5 12.9)
            int dataSize = chartModel.priceModelListVisible.size();
            //float keduCapacity = dataSize / keduCount * 1.0f;
            float keduCapacity = 3;
            int keduCapacityI = (int) Math.floor(keduCapacity);
            //two point px distance
            float dataXStride = kedu / keduCapacityI * 1.000f;

            for (int i = 0; i < keduCount; i++) {
                int index = i * keduCapacityI;
                PriceModel priceModel = chartModel.priceModelListVisible.get(index);
                String time = priceModel.time;
                //startY+3-->startY+30
                canvas.drawText(time, startX + (i * kedu), startY + 30, numberPaint);
            }
            //3,draw line,strip
            //draw points
            chartModel.axisY.setAxisMaxMinValue(yMaxValue,yMinValue);
            float unit = chartModel.axisY.getUnit();

            Path path=new Path();
            Log.i("info2","startY="+startY);
            for (int i = 0; i < dataSize; i++) {
                PriceModel priceModel = chartModel.priceModelListVisible.get(i);
                float fiveDayAvgPrice = priceModel.getFiveDayAvgPrice();
                float priceDiff = fiveDayAvgPrice - yMinValue;
                //int  pricePx= (int) (fiveDayAvgPrice*unit);
                float pricePx = (priceDiff * unit);
                float priceY = startY - pricePx;
                float priceX=startX+((i+1)*dataXStride);

                //canvas.drawCircle(priceX, priceY, 5, pointPaint);
                Log.i("info2","priceX="+priceX+"-priceY="+priceY);
                if(i==0){
                    path.moveTo(priceX,priceY);
                }else{
                    path.lineTo(priceX,priceY);
                }
            }
            //draw lines
            canvas.drawPath(path,pointPaint);
            //draw strip(a vertical line through vertical rectangle)
            for (int i = 0; i <dataSize ; i++) {
         PriceModel priceModel=chartModel.priceModelListVisible.get(i);
                //draw rect
            float openPrice=    priceModel.getOpenPrice();
                float closePrice=priceModel.getClosePrice();
            float openPriceDiff=    openPrice-yMinValue;
             float openPriceY=   openPriceDiff*lenValueAspect;
                openPriceY=startY-openPriceY;
                //float openPriceX=
                //int openPriceY=openPrice*
                //draw vertical line
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null)
                sfh.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * 触屏事件监听
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 图片的线程运行
     */
    class ImageRunnable implements Runnable {
        @Override
        public void run() {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if (end - start < 500) {
                handler.postDelayed(this, 200 - (end - start));
            } else {
                handler.post(this);
            }
        }
    }

    /**
     * SurfaceView视图状态发生改变，响应此函数
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        System.out.println("ImageSurfaceView is surfaceChanged");
    }

    /**
     * SurfaceView视图消亡时，响应此函数
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        System.out.println("ImageSurfaceView is surfaceDestroyed");
    }
}