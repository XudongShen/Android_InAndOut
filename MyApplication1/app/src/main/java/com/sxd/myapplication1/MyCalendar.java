package com.sxd.myapplication1;

/**
 * Created by SXD on 2016/11/28.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyCalendar {

    MyCalendar(){

    }

    static public String getTime_str(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日 HH:mm ");
        Date curDate =  new Date(time);
        String str = formatter.format(curDate);
        return str;
    }

    static public String getTime_str(String time_str){
        long time = Long.parseLong(time_str);
        return getTime_str(time);
    }

    static public String getCurrentTime_str(){
        return getTime_str(System.currentTimeMillis());
    }

    static public long getCurrentTime_long(){
        return System.currentTimeMillis();
    }

    static public int getCurrentYear(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return Integer.parseInt(str);
    }

    static public int getCurrenMonth(){
        SimpleDateFormat formatter = new SimpleDateFormat("MM");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return Integer.parseInt(str);
    }

    static public String getDay_str(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }

    static public String getMonth_str(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }

    static public boolean isThisDay(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(time);
        return formatter.format(curDate).equals(getDay_str());
    }

    static public boolean isThisMonth(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月");
        Date curDate = new Date(time);
        return formatter.format(curDate).equals(getMonth_str());
    }

    static public boolean isChoosenDay(String date,long time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(time);
        return formatter.format(curDate).equals(date);
    }

    static public boolean isChoosenMonth(String date,long time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月");
        Date curDate = new Date(time);
        return formatter.format(curDate).equals(date);
    }

    static public boolean isChoosenYear(String date,long time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年");
        Date curDate = new Date(time);
        return formatter.format(curDate).equals(date);
    }

    static public int getDayCountInMonth(int year,int month){
        boolean isLoop = (year%4==0)&&(year%100!=0);
        int dayCount = 0;
        switch(month){
            case 1:case 3:case 5:case 7:case 8:case 10:case 12:dayCount = 31;break;
            case 2:dayCount = isLoop ? 29 : 28;break;
            default:dayCount = 30;break;
        }
        return dayCount;
    }
}
