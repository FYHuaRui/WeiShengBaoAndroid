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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.fangyuan.weipanbao.model.PriceModel;
import com.fangyuan.weipanbao.model.StripLineChartModel;
import com.fangyuan.weipanbao.util.MathUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/13 0013.
 * <p>
 * support show many line,strip
 * content --》view width height
 * scale,move
 * <p>
 * 1,draw x y axis,kedu, data diff/px diff;
 * 2,draw line
 * <p>
 * 每个priceModel 图表中对应的x轴区间和此区间 中线，这种坐标到 对象的关系 保存起来，
 */

public class LineChartStripSV2 extends SurfaceView implements SurfaceHolder.Callback, ScaleGestureDetector.OnScaleGestureListener {


    //用于控制SurfaceView
    private SurfaceHolder sfh;
    private Handler handler = new Handler();
    private ImageRunnable imageRunnable = new ImageRunnable();
    private Paint axisPaint;
    private Canvas canvas;
    private Matrix matrix;


    private float mStripScaleFactor = 1.0f;
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
     * 从此scaleFactor 开始，后面的scaleFactor（假设名为 factorN） 都和它比较，达到一定差值，开始 缩放数据，
     * 则factorN成为新的 baseScaleFactor,
     * 所以叫base，即基准值
     *
     */
    private float baseScaleFactor;

    /**
     * init offset,unit dp
     */
    private int stripSideOffsetDp=6;
    /**
     * max offset,dp
     */
    private int stripSideOffsetDpMax=9;
    private int stripSideOffsetDpMin=2;
    /**
     * init offset,px
     */
    private float stripSideOffsetPx=-1;
    private float stripSideOffsetPxMax=0;
    private float stripSideOffsetPxMin=0;
    /**
     * 绘图时，每个priceModel对象 和它对应的x轴坐标 形成的map
     */
    Map<PriceModel, Float> priceModelXCoordMap = new HashMap<>();
    private final Paint stripPaint;

    float lastX;
    private final ScaleGestureDetector scaleGestureDetector;
    /**
     * strip width scale aspect,
     */
    private int scaleDiffInt;

    /**
     * SurfaceView初始化函数
     */
    public LineChartStripSV2(Context context, AttributeSet attrs) {
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

        stripPaint = new Paint();
        stripPaint.setColor(Color.RED);
        stripPaint.setStyle(Paint.Style.FILL);

        setFocusable(true);

        scaleGestureDetector = new ScaleGestureDetector(context, this);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        stripSideOffsetPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, stripSideOffsetDp, displayMetrics);
        //stripSideOffsetPxMax = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, stripSideOffsetDpMax, displayMetrics);
        //stripSideOffsetPxMin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, stripSideOffsetDpMin, displayMetrics);
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
            //use black color clear canvas
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
            int yAxisLen = screenH - chartModel.topPadding - chartModel.bottomPadding;
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
            float lenValueAspect = axisLength / valueDiff;
            for (int i = 0; i < keduCount; i++) {
                float startX2 = yMinValue + i * valueUnit;
                startX2 = MathUtil.get3FractionFloat(startX2);
                if (i != 0) {
                    canvas.drawText(startX2 + "", startX, startY - i * pxUnit, numberPaint);
                }
            }
            //2,draw y axis and x axis time text
            endY = chartModel.topPadding;
            //chartModel.axisX.setViewSide(screenW);
            int xAxisLen = screenW - chartModel.leftPadding - chartModel.rightPadding;
            int dataSize = chartModel.getDataSize();
            chartModel.axisX.setAxisPxLength(xAxisLen, dataSize);
            kedu = chartModel.axisX.kedu;
            //keduCount = chartModel.axisX.keduCount;
            Log.i("info2", "axisX keduCount=" + keduCount);
            Log.i("info2", "axisX kedu=" + kedu);
//----------------------
            int keduStride = chartModel.axisX.getKeduStride();

            canvas.drawLine(startX, startY, startX, endY, axisPaint);
            for (int i = 0; i < dataSize; i++) {
                // if(i==0){
                //canvas.drawLine(startX , startY, startX , endY, axisPaint);
                //每个大的刻度处，画上 时间 文本
                // PriceModel priceModel = chartModel.priceModelListVisible.get(i);
                //String time = priceModel.getTime();
                //canvas.drawText(time,startX + ((i+1) * kedu), startY,numberPaint);
                // }else
                if ((i + 1) % keduStride == 0) {
                    canvas.drawLine(startX + ((i + 1) * kedu), startY, startX + ((i + 1) * kedu), endY, axisPaint);
                    //每个大的刻度处，画上 时间 文本
                    PriceModel priceModel = chartModel.priceModelListVisible.get(i);
                    String time = priceModel.getTime();
                    canvas.drawText(time, startX + ((i + 1) * kedu), startY, numberPaint);

                    priceModelXCoordMap.put(priceModel, startX + ((i + 1) * kedu));
                } else {
                    PriceModel priceModel = chartModel.priceModelListVisible.get(i);
                    priceModelXCoordMap.put(priceModel, startX + ((i + 1) * kedu));
                }
            }
            //数据分布分为两种情况，
            //1,平铺，所有数据平均铺开，显示在屏幕上
            //2，顺序排列，从左至右顺序排列，不一定到达屏幕宽度
            //3,draw line,strip
            //draw points
            chartModel.axisY.setAxisMaxMinValue(yMaxValue, yMinValue);
            float unit = chartModel.axisY.getUnit();

            Path path = new Path();
            Log.i("info2", "startY=" + startY);
            int xAxisKedu = chartModel.axisX.getKedu();
            for (int i = 0; i < dataSize; i++) {
                PriceModel priceModel = chartModel.priceModelListVisible.get(i);
                float fiveDayAvgPrice = priceModel.getFiveDayAvgPrice();
                float priceDiff = fiveDayAvgPrice - yMinValue;
                //int  pricePx= (int) (fiveDayAvgPrice*unit);
                float pricePx = (priceDiff * unit);
                float priceY = startY - pricePx;
                float priceX = startX + ((i + 1) * xAxisKedu);

                //canvas.drawCircle(priceX, priceY, 5, pointPaint);
                Log.i("info2", "priceX=" + priceX + "-priceY=" + priceY);
                if (i == 0) {
                    path.moveTo(priceX, priceY);
                } else {
                    path.lineTo(priceX, priceY);
                }
            }

            //draw lines
            canvas.drawPath(path, pointPaint);
            //draw strip(a vertical line through vertical rectangle)
            for (int i = 0; i < dataSize; i++) {
                PriceModel priceModel = chartModel.priceModelListVisible.get(i);
                //draw rect
                float openPrice = priceModel.getOpenPrice();
                float closePrice = priceModel.getClosePrice();
                float openPriceDiff = openPrice - yMinValue;
                float openPriceY = openPriceDiff * lenValueAspect;
                openPriceY = startY - openPriceY;
                Float modelXCoord = priceModelXCoordMap.get(priceModel);
                Log.i("info2","strip scale factor="+mStripScaleFactor);
                //stripSideOffsetPx*= mStripScaleFactor;
                float xOffset=stripSideOffsetPx*scaleDiffInt;
                float openPriceX = modelXCoord - stripSideOffsetPx;
                Log.i("info2","modelXCoord="+modelXCoord+"-stripSideOffsetPx="+stripSideOffsetPx);
                float closePriceDiff = closePrice - yMinValue;
                float closePriceY = closePriceDiff * lenValueAspect;
                closePriceY = startY - closePriceY;
                float closePriceX = modelXCoord + stripSideOffsetPx;

                int color = priceModel.getColor();
                stripPaint.setColor(color);

                canvas.drawRect(openPriceX, openPriceY, closePriceX, closePriceY, stripPaint);
                Log.i("info2", "openX=" + openPriceX + "-openY=" + openPriceY + "-closeX=" + closePriceX + "-closeY=" + closePriceY);
                float topPrice = priceModel.getTopPrice();
                float bottomPrice = priceModel.getBottomPrice();

                float topPriceDiff = topPrice - yMinValue;
                float topPriceDiffPx = topPriceDiff * lenValueAspect;
                float topPriceY = startY - topPriceDiffPx;

                float bottomPriceDiff = bottomPrice - yMinValue;
                float bottomPriceDiffPx = bottomPriceDiff * lenValueAspect;
                float bottomPriceY = startY - bottomPriceDiffPx;

                float topPriceX = priceModelXCoordMap.get(priceModel);
                canvas.drawLine(topPriceX, topPriceY, topPriceX, bottomPriceY, stripPaint);
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
        //区分单点 和两点触摸，
        //单点触摸 区分 长按和拖动
        //两点触摸用于缩放
        int pointerCount = event.getPointerCount();
        Log.i("info2", "pointerCount=" + pointerCount);
        switch (pointerCount) {
            case 1:
                //判断水平滑动距离
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float currX = event.getX();
                        float xDiff = currX - lastX;
                        //Log.i("info2","xDiff="+xDiff);
                        int moveDelta = (int) xDiff / 10;
                        Log.i("info2", "moveDelta=" + moveDelta);
                        boolean ifReDraw = chartModel.cutPriceModel2(-moveDelta);
                        if (ifReDraw) {
                            invalidate();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                break;
            case 2:
                scaleGestureDetector.onTouchEvent(event);
                break;
        }

        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        //scaleFactor，缩放图表时，同时可以缩放strip
        float scaleFactor = scaleGestureDetector.getScaleFactor();
        if(baseScaleFactor !=0){
            //float aspect = scaleFactor / baseScaleFactor;
            float diff = scaleFactor - baseScaleFactor;
           float absDiff= Math.abs(diff);
            if(absDiff>0.1){
                if(diff>0){
                    //chart scale up,
                    mStripScaleFactor=1+diff;
                }else if(diff<0){
                    mStripScaleFactor=1-diff;
                }
            }
            //Log.i("info2","scalePactor aspect="+aspect);
        }
        //mStripScaleFactor = scaleFactor;
        Log.i("info2", "scaleFactor=" + scaleFactor);
        if (baseScaleFactor == 0) {
            baseScaleFactor = scaleFactor;
            //return false;
            // return true;
        } else {
       /* if(scaleFactor==1.0){
            //return false;
            return true;
        }else*/
            if (scaleFactor < 1.0) {
                //缩小，最多显示50条数据
             float diff=   scaleFactor - baseScaleFactor;
                float abs = Math.abs(diff);
                if (abs >= 0.1) {
                    float scaleAspect = 1 + abs;
                    Log.i("info2", "scale down,scaleAspect=" + scaleAspect);
                    boolean ifScaleDataSuccess = chartModel.scaleData(scaleAspect);
                    if (ifScaleDataSuccess) {
                      scaleDiffInt= (int) (diff*10);
                        invalidate();
                    }
                    baseScaleFactor = scaleFactor;
                }

            } else if (scaleFactor > 1.0) {
                //放大，最少显示15条数据
               float diff= scaleFactor - baseScaleFactor;
                float abs = Math.abs(diff);
                if (abs >= 0.1) {
                    float scaleAspect = 1 - abs;
                    boolean ifScaleDataSuccess = chartModel.scaleData(scaleAspect);
                    //
                    if (ifScaleDataSuccess) {
                      //scale data success,so chart view need scale,so strip need scale
                        //if scale data fail,then chart view donot scale,strip neednot scale
                        scaleDiffInt = (int) (diff*10);
                        invalidate();

                    }
                }
                baseScaleFactor = scaleFactor;
            }
        }
        //return true;
        
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        baseScaleFactor =0;
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