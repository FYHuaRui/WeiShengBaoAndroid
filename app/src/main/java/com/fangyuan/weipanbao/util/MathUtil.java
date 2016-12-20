package com.fangyuan.weipanbao.util;

/**
 * Created by Administrator on 2016/12/15.
 */

public class MathUtil {

    public static boolean isPrime(int a){
        if (a <= 1) {  //质数不能小于1
            //System.out.println("不是质数");
            return false;
        }else if(a==2 ||a==3){  //单独判断，想不到怎么弄
           // System.out.println("是质数");
            return true;
        }
        int k = (int) Math.sqrt(a);  //取判断数的平方根的整数
        for (int i = 2; i <= k; i++) {
            if(a % i == 0) {     //判断
                //System.out.println("不是质数");
                return false;
            }else{
                //System.out.println("是质数");
                return true;
            }
        }
  return false;
    }

    public static float get3FractionFloat(float originalFloat){
        float valueFormat=  (float)(Math.round(originalFloat*1000))/1000;
        return valueFormat;
    }
    public static float get1FractionFloat(float originalFloat){
        float valueFormat=  (float)(Math.round(originalFloat*10))/10;
        return valueFormat;
    }
}
