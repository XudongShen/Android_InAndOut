package com.sxd.myapplication1;

/**
 * Created by SXD on 2016/11/28.
 */
import android.os.AsyncTask;

import java.io.UnsupportedEncodingException;

import static com.sxd.myapplication1.SpSaveRead.read;
import static com.sxd.myapplication1.SpSaveRead.save;

public class MyRecord {
    private long id;
    private long time;
    private int costOrIncome;
    private double amount;
    private String info;
    private String storedString;
    private String displayString;
    private String displayInHomePage;

    private String amount_str;
    private String info_str;

    static public int COST = 1;
    static public int INCOME = 0;

    MyRecord(int costOrIncome,double amount,String info){
        this.id = MyConst.maxRecordId + 1;
        this.time = MyCalendar.getCurrentTime_long();
        this.costOrIncome = costOrIncome;
        this.amount = amount;
        this.amount_str = (""+amount).replace('.','-');
        this.info = info;
        this.info_str = info.replace(' ','-');
        this.storedString = ""+time+'@'+costOrIncome+'@'+amount_str+'@'+info_str;
        this.displayString = MyCalendar.getTime_str(time)+" "+((costOrIncome==COST)?'-':'+')+amount+"\n"+info;
        this.displayInHomePage = MyCalendar.getTime_str(time)+" "+((costOrIncome==COST)?'-':'+')+amount+"  "+info;
    }

    MyRecord(long id){
        this.id = id;
        recordRead();
        this.displayString = MyCalendar.getTime_str(time)+" "+((costOrIncome==COST)?'-':'+')+amount+"\n"+info;
        this.displayInHomePage = MyCalendar.getTime_str(time)+" "+((costOrIncome==COST)?'-':'+')+amount+"  "+info;
    }

    public void recordSave(){
        MyConst.maxRecordId ++;
        save("maxRecordId",""+MyConst.maxRecordId);
        save("Record"+id,storedString);
    }

    private void recordRead(){
        if(id<=MyConst.maxRecordId&&id>0) {

            storedString = read("Record" + id);
            String[] temp = storedString.split("@");
            time = Long.parseLong(temp[0]);
            costOrIncome = Integer.parseInt(temp[1]);
            amount = Double.parseDouble(temp[2].replace('-','.'));
            info = temp[3].replace('-',' ');
        }
    }

    public long getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public int getCostOrIncome() {
        return costOrIncome;
    }

    public double getAmount() {
        return amount;
    }

    public String getInfo() {
        return info;
    }

    public String getStoredString() {
        return storedString;
    }

    public String getDisplayString() {
        return displayString;
    }

    public String getDisplayInHomePage() {
        return displayInHomePage;
    }
}
