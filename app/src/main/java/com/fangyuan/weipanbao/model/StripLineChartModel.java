package com.fangyuan.weipanbao.model;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/12/14 0014.
 *
 * 此种类型 图表，数据平铺，占满屏幕宽度
 *
 * 从屏幕左边顺序堆放的 图表，用 另外一个surfaceView 来实现，
 */

public class StripLineChartModel {
    public AxisModel axisX;
    public AxisModel axisY;
    /**
     * 所有价格数据
     */
    public List<PriceModel> allPriceModelList;
    /**
     * 从所有价格数据中 截取的，要画到界面上
     * 如果 数据很少，截取后，还是原始数据，即所有价格数据 全部显示
     */
    public List<PriceModel> priceModelListVisible;
    /**
     * 此数据用于缩放图表，即界面显示的一组数据，其中的中间数据在所有数据的index
     */
    private int centerIndexVisible = -1;
    /**
     * 最多截取30条数据
     */
    int initDataRange = 30;
    //int stripSideOffset=0
    /**
     * start index,end index 是所有数据list中的index，用于平移visible data zone
     */
    private int startIndexVisible = 0,
            endIndexVisible = initDataRange - 1;
    public int leftPadding;
    public int bottomPadding;
    public int topPadding;
    public int rightPadding;

    float maxPrice;
    float minPrice;

    /**
     * 数据平均分布
     */
    public static final int LAY_TYPE_AVG=10;
    /**
     * 数据线性分布，顺序排列
     */
    public static final int LAY_TYPE_ORDER=20;
    public int dataLayType=LAY_TYPE_AVG;

    /**
     * 此参数，针对数据很少，不能占满屏幕宽度，设置 虚拟的数据个数，即 计划屏幕宽度内
     * 要显示多少个数据---废弃，不用XXXX
     * @return
     */
    public int fakeDataSize=-1;

    public int getFakeDataSize() {
        return fakeDataSize;
    }
public int getDataSize(){
        return priceModelListVisible.size();
}
    public void setFakeDataSize(int fakeDataSize) {
        this.fakeDataSize = fakeDataSize;
    }


    public StripLineChartModel(int leftPadding, int rightPadding, int bottomPadding, int topPadding, AxisModel axisX, AxisModel axisY, List<PriceModel> allPriceModelList) {
        this.leftPadding = leftPadding;
        this.rightPadding = rightPadding;
        this.bottomPadding = bottomPadding;
        this.topPadding = topPadding;

        this.axisX = axisX;
        this.axisY = axisY;

        this.allPriceModelList = allPriceModelList;

        initEndIndexVisible();
        cutPriceModel2(0);
    }

    /**
     * 初始化 可见数据的终点索引
     */
    private void initEndIndexVisible() {
        if (null != allPriceModelList && !allPriceModelList.isEmpty()) {
            int size = allPriceModelList.size();
            //if (size < 30) {
              if(size< initDataRange){
                endIndexVisible = size - 1;
            }
            Log.i("info2","init,startIndex="+0+"-endIndex="+endIndexVisible);
        }
    }
    /**
     * call when drag chart horizontally
     *
     * @param moveDelta 正数 表示list集合 index 增大（右移），0 index 不动，
     *                  负数 index 减小（左移）
     * @return true, need redraw,false donot need redraw
     */

    public boolean cutPriceModel2(int moveDelta) {
        //1,cut
        if (null != allPriceModelList && !allPriceModelList.isEmpty()) {
            int size = allPriceModelList.size();
            int maxIndex = size - 1;

            int startIndexTemp = startIndexVisible;
            int endIndexTemp = endIndexVisible;

            startIndexTemp += moveDelta;
            endIndexTemp += moveDelta;
            if (startIndexTemp < 0 || startIndexTemp > maxIndex) {
                return false;
            }
            if (endIndexTemp > maxIndex || endIndexTemp < 0) {
                return false;
            }
            startIndexVisible = startIndexTemp;
            endIndexVisible = endIndexTemp;
            Log.i("info2","startIndex="+startIndexVisible+"-endIndex="+endIndexVisible);
            calculCenterIndex2();

            List<PriceModel> priceModelsTemp = allPriceModelList.subList(startIndexVisible, endIndexVisible + 1);
            priceModelListVisible = new ArrayList<>(priceModelsTemp);
        }
        //2,calculate max price,min price
        calculVisibleMaxMinData();

        return true;
    }

    /**
     * cut based on current new start index visible,end index visible
     * @return
     */
    public void cutPriceModel2() {
        //1,cut
        if (null != allPriceModelList && !allPriceModelList.isEmpty()) {
            int size = allPriceModelList.size();
            Log.i("info2","allList size="+size+"startIndex="+startIndexVisible+"-endIndex="+endIndexVisible);
            List<PriceModel> priceModelsTemp = allPriceModelList.subList(startIndexVisible, endIndexVisible + 1);
            priceModelListVisible = new ArrayList<>(priceModelsTemp);
        }

        //2,calculate max price,min price
        calculVisibleMaxMinData();
    }

    private void calculCenterIndex2() {
        int visibleCount = endIndexVisible - startIndexVisible + 1;
        if (visibleCount % 2 == 0) {
            centerIndexVisible = visibleCount / 2 - 1;
        } else {
            centerIndexVisible = (visibleCount + 1) / 2 - 1;
        }
    }

    /**
     * 计算截取的数据中 价格最大的对象，最小的对象
     *
     * 可能要在子线程跑，界面有点卡
     */
    private void calculVisibleMaxMinData() {
        if (null != priceModelListVisible && !priceModelListVisible.isEmpty()) {
            List<Float> maxPriceList = new ArrayList<>();
            List<Float> minPriceList=new ArrayList<>();

            int size = priceModelListVisible.size();
            for (int i = 0; i < size; i++) {
                PriceModel priceModel = priceModelListVisible.get(i);
                float maxPrice = priceModel.getMaxPrice();
                maxPriceList.add(maxPrice);
            float minPrice=    priceModel.getMinPrice();
                minPriceList.add(minPrice);
            }
            maxPrice = Collections.max(maxPriceList);
            minPrice = Collections.min(minPriceList);
        }
    }

    /**
     * called when scale chart
     * <p>
     * 从数据层缩放，就是 选定的数据范围扩大 或缩小
     *
     * @param scaleFactor 缩放比例，大于1.0 表示 放大，小于1.0 缩小,1.0 不缩放
     *                    <p>
     *                    放大时，显示的数据越来越少，最少10条，如果到了10条，放大操作无效
     *                    缩小时，显示的数据越来越多，最多50条，如果到了50条，缩小操作无效，
     *
     * @return true,数据缩放成功，可以用来更新界面，false，数据缩放失败，不能用来更新界面
     */


    public boolean scaleData(float scaleFactor) {
        if (null == priceModelListVisible || priceModelListVisible.isEmpty()) {
            return false;
        }
        //缩放逻辑
        //1,判断当前数据总数 能否缩放
        //2,如果可以缩放，缩放越界了，要调整 startIndex，endIndex
        //int originalSize = priceModelListVisible.size();
        /*if (size <= 10 || size >= 50) {
            return;
        }*/
        //int indexOffset = moveIndexInScale(scaleFactor);
        boolean ifScaleDataSuccess = moveIndexInScale(scaleFactor);
        if(ifScaleDataSuccess){
            cutPriceModel2();
            return true;
        }
        //size<=10,cannot scale up;size >=50,cannot scale down
        /*if (scaleFactor<1.0){
            //scale down
            if(originalSize>=50){
                return false;
            }
            //startIndex endIndex expand
            int startIndexTemp=startIndexVisible;
            int endIndexTemp=endIndexVisible;
            startIndexTemp-=indexOffset;
            endIndexTemp+=indexOffset;
            if(startIndexTemp<0){
                startIndexTemp=0;
            }
            if(endIndexTemp>originalSize-1){
                endIndexTemp=originalSize-1;
            }
            startIndexVisible=startIndexTemp;
            endIndexVisible=endIndexTemp;
            cutPriceModel2();
            return true;
        }else if(scaleFactor>1.0){
            //scale up
            if(originalSize<=10){
                return false;
            }
            //startIndex endIndex shrink
            int startIndexTemp=startIndexVisible;
            int endIndexTemp=endIndexVisible;
            startIndexTemp+=indexOffset;
            endIndexTemp-=indexOffset;
            if
        }*/


        /*int startIndexVisibleTemp = startIndexVisible;
        int endIndexVisibleTemp = endIndexVisible;
        if (scaleFactor < 0) {
            //缩小可见数据范围
            startIndexVisibleTemp += scaleFactor;
            endIndexVisibleTemp -= scaleFactor;
            if (startIndexVisibleTemp > centerIndexVisible || endIndexVisibleTemp < centerIndexVisible) {
                return;
            } else {
                startIndexVisible = startIndexVisibleTemp;
                endIndexVisible = endIndexVisibleTemp;

                List<PriceModel> priceModelsTemp = allPriceModelList.subList(startIndexVisible, endIndexVisible + 1);
                priceModelListVisible = priceModelsTemp;
            }

        } else if (scaleFactor > 0) {
            //放大可见数据范围
            //startindex endIndex not out of bound(visible bound)
            //exceed bound;
            //cut from all data
            startIndexVisibleTemp -= scaleFactor;
            endIndexVisibleTemp += scaleFactor;
            if (startIndexVisibleTemp < 0) {
                return;
            }
            if (endIndexVisibleTemp > allPriceModelList.size() - 1) {
                return;
            }
            startIndexVisible = startIndexVisibleTemp;
            endIndexVisible = endIndexVisibleTemp;

            List<PriceModel> priceModelsTemp = allPriceModelList.subList(startIndexVisible, endIndexVisible + 1);
            priceModelListVisible = priceModelsTemp;

        }*/
        return false;
    }

    /**
     * @param scaleFactor
     * @return return indexVisibleOffset，起始索引的偏移量，可能导致 起始索引 越界，
     * 或者导致 截取的数据太多/太少（非法），所以接收此返回值的地方，需要判断 上述特殊情况
     */
    private boolean moveIndexInScale(float scaleFactor){
        //return true,scale data success,notify update chart
        //return false,scale data fail,not need update chart
        int allSize = allPriceModelList.size();
        int visibleDataSize=  getDataSize();
        int newDataSize = (int) (visibleDataSize * scaleFactor);
        Log.i("info2","dataSize="+visibleDataSize+"-newDataSize="+newDataSize);
        //if(scaleFactor<1.0){
            if(scaleFactor>1.0){
            //chart scale down,dataSize +
            //index expand
            if(visibleDataSize>=50){
                return false;
            }
            if(newDataSize>50){
                newDataSize=50;
            }
            int diff = newDataSize - visibleDataSize;
         int offset=   diff/2;
            startIndexVisible-=offset;
            endIndexVisible+=offset;
            if(startIndexVisible<0){
                startIndexVisible=0;
            }
            if(endIndexVisible>allSize-1){
                endIndexVisible=allSize-1;
            }
            return true;
        //}else if(scaleFactor>1.0){
            }else if(scaleFactor<1.0){
            //chart scale up,dataSize -
            //index shrink
            if(visibleDataSize<=10){
                return false;
            }
            if(newDataSize<10){
                newDataSize=10;
            }
            int diff=visibleDataSize-newDataSize;
            Log.i("info2","scale up,diff="+diff);
           int offset= diff/2;
            startIndexVisible+=offset;
            endIndexVisible-=offset;
            return true;
        }
        //int abs = Math.abs(newDataSize - dataSize);
        //int  indexOffset =  abs / 2;



        //return indexOffset;
        return false;
    }

    /**
     *
     * @param scaleFactor 缩放因子
     * @return true，缩放因子有效，界面 将会缩放，false，缩放因子无效，界面不会缩放
     */
    private boolean checkScaleFactorValid(float scaleFactor){
        int dataSize=  getDataSize();
        float newDataSize = dataSize * scaleFactor;
        if(newDataSize<=10||newDataSize>=50){
            return false;
        }
        return true;
    }

    public float getMaxPrice() {
        return maxPrice;
    }

    public float getMinPrice() {
        return minPrice;
    }
}
