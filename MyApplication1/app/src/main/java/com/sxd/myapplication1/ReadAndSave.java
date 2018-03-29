package com.sxd.myapplication1;

/**
 * Created by SXD on 2016/12/2.
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReadAndSave {

    public String readUnderUser(String title){
        return this.read(MyConst.UserName+'@'+title);
    }

    public Boolean saveUnderUser(String title,String content){
        return this.save(MyConst.UserName+'@'+title,content);
    }

    public String read(String title) {
        URL url;
        String result = "";
        try {
            url = new URL(MyConst.ReadURL+title);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setConnectTimeout(5000);
            urlConn.setReadTimeout(2000);

            if (urlConn.getResponseCode() == 200) {
                InputStream is = urlConn.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while(-1 != (len = is.read(buffer))){
                    os.write(buffer,0,len);
                    os.flush();
                }
                result = os.toString();
                result = result.substring(1,result.length()-1);
            }
            urlConn.disconnect();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Boolean save(String title,String content){
        URL url;
        String result = "";
        try {
            url = new URL(MyConst.SaveURL+title+'/'+content);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setConnectTimeout(5000);
            urlConn.setReadTimeout(2000);

            if (urlConn.getResponseCode() == 200) {
                InputStream is = urlConn.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while(-1 != (len = is.read(buffer))){
                    os.write(buffer,0,len);
                    os.flush();
                }
                result = os.toString();
                result = result.substring(1,result.length()-1);
            }

            urlConn.disconnect();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if(result.equals(title+".txt saved success")){
            return true;
        }
        else
            return false;
    }

}

