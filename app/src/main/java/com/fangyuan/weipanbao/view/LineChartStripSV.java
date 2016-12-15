package com.fangyuan.weipanbao.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.fangyuan.weipanbao.model.StripLineChartModel;

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

            chartModel.axisY.setViewSide(screenH);
            float kedu = chartModel.axisY.kedu;
            int keduCount=chartModel.axisY.keduCount;
            for (int i = 0; i <keduCount; i++) {
                canvas.drawLine(startX,startY-(i*kedu),endX,endY-(i*kedu),axisPaint);
            }
            //2,draw y axis
             endY=chartModel.topPadding;
            chartModel.axisX.setViewSide(screenW);
             kedu=chartModel.axisX.kedu;
            keduCount=chartModel.axisX.keduCount;
            Log.i("info2","axisX keduCount="+keduCount);
            Log.i("info2","axisX kedu="+kedu);
            for (int i = 0; i < keduCount; i++) {
                canvas.drawLine(startX+(i*kedu),startY,startX+(i*kedu),endY,axisPaint);
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