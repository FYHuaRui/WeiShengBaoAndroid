package com.fangyuan.weipanbao.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Administrator on 2016/12/13 0013.
 *
 * support many lines,rectangle
 *
 * content --》view width height
 * 1,draw x y axis,kedu, data diff/px diff;
 * 2,draw line
 *
 */

public class LineChartRectangleSV extends SurfaceView implements SurfaceHolder.Callback {



    //用于控制SurfaceView
    private SurfaceHolder sfh;
    private Handler handler = new Handler();
    private ImageRunnable imageRunnable = new ImageRunnable();
    private Paint paint;
    private Canvas canvas;
    private Matrix matrix;

    /**图片的坐标*/
    private float imageX, imageY;
    /**获取的图片*/
    private Bitmap bmp;
    /**图片宽高*/
    private float bmpW, bmpH;
    /**屏幕大小*/
    private int screenW, screenH;

    /**
     * SurfaceView初始化函数
     */
    public LineChartRectangleSV(Context context, AttributeSet attrs) {
        super(context, attrs);
        sfh = this.getHolder();
        sfh.addCallback(this);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
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
    /**
     * 游戏绘图
     */
    public void draw() {
        try {
            canvas = sfh.lockCanvas();
            canvas.drawRGB(0, 0, 0);
            canvas.save();
//绘制
            canvas.drawBitmap(bmp, matrix, paint);
            System.out.println("绘制图像了吗？");
            canvas.restore();
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
    class ImageRunnable implements Runnable{
        @Override
        public void run() {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if (end - start < 500) {
                handler.postDelayed(this, 200 - (end-start));
            }else{
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