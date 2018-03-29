package com.sxd.myapplication1;

/**
 * Created by SXD on 2016/11/21.
 */

public class MyConst {
    static final int HOMEPAGE = 1;
    static final int INANDOUT = 2;
    static final int MYPLAN = 3;
    static final int SETTING = 4;
    static int current_page = 1;
    static String settings_Remember = "off";
    static String settings_Attention = "off";
    static long maxRecordId = 0;
    static int spinner_Year = 0;
    static int spinner_Month = 0;
    static int spinner_Day = 0;
    static boolean spinner_Year_Changed = false;
    static boolean spinner_Month_Changed = false;
    static int myPlanDailyPlan = 0;
    static int myPlanMonthlyPlan = 0;
    static String UserName = "No User";
    static final String ReadURL = "http://115.159.123.157:8999/api/User/GetRead/";
    static final String SaveURL = "http://115.159.123.157:8999/api/User/GetSave/";
    static Boolean login = false;
}
