package com.sxd.myapplication1;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by SXD on 2016/11/28.
 */

public class SpSaveRead {

    static Context context;

    static public void setContext(Context context1){
        context = context1;
    }

    static public void save(String title,String content){
        SharedPreferences sp = context.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(MyConst.UserName+'@'+title,content);
        editor.commit();
    }

    static public String read(String title){
        SharedPreferences sp = context.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        String data = sp.getString(MyConst.UserName+'@'+title,"");
        return data;
    }

    static public String getCurrentUserName(){
        SharedPreferences sp = context.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        String data = sp.getString("CurrentUserName","");
        return data;
    }

    static public void saveCurrentUserName(String userName){
        SharedPreferences sp = context.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("CurrentUserName",userName);
        editor.commit();
    }

    static public String readUserPassword(String userName){
        SharedPreferences sp = context.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        String data = sp.getString(userName,"");
        return data;
    }

    static public void saveUserPassword(String userName,String password){
        SharedPreferences sp = context.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(userName,password);
        editor.commit();
    }

    static public void saveLocalSetting(String title,String content){
        SharedPreferences sp = context.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(title,content);
        editor.commit();
    }

    static public String readLocalSetting(String title){
        SharedPreferences sp = context.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        String data = sp.getString(title,"");
        return data;
    }
}
