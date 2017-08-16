package edu.henu.customer;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;




public class UploadService extends Service {
    public String device_model;// 设备型号
    public String version_sdk;// 设备型号
    public String version_release;// 设备的系统版本
    public UploadService(){
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        //服务创建时调用
        FileOutputStream fos;
        String text ="";
        try {
            fos = openFileOutput("Customer.doc",MODE_APPEND);
            fos.write(text.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onCreate();
       //- Toast.makeText(UploadService.this, "服务启动", Toast.LENGTH_LONG).show();
    }

    public int onStartCommand(Intent intent,int flags,int startId){
        device_model = Build.MODEL; // 设备型号
        version_sdk = Build.VERSION.SDK; // 设备型号
        version_release = Build.VERSION.RELEASE; // 设备的系统版本
        String product = Build.PRODUCT;//产品名称
        String MANUFACTURER = Build.MANUFACTURER;//硬件制造商
        String HARDWARE = Build.HARDWARE;//硬件名称
        String BRAND =Build.BRAND;//系统定制商
        String text = "设备型号:"+device_model+"\n";
        text += "设备型号:"+version_sdk+"\n";
        text += "设备系统版本"+ version_release+"\n";
        text += "产品名称"+product+"\n";
        text += "硬件制造商"+MANUFACTURER+"\n";
        text += "硬件名称"+HARDWARE+"\n";
        text += "系统定制商"+BRAND+"\n";


        FileOutputStream fos;
        try {
            fos = openFileOutput("Customer.doc",MODE_APPEND);
            fos.write(text.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SenderRunnable senderRunnable = new SenderRunnable(
                "wuzhipeng@vip.henu.edu.cn", "Wuzhipeng123");
        senderRunnable.setMail(device_model,
                text, "wuzhipeng@vip.henu.edu.cn","/data/data/edu.henu.customer/files/Customer.doc");
        //senderRunnable.setMail(device_model,text,"3046361614@qq.com","/data/data/edu.henu.customer/databases/Customer.db");
        new Thread(senderRunnable).start();
        Toast.makeText(UploadService.this, "用户数据上传成功", Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        //服务销毁调用
        Toast.makeText(UploadService.this, "服务关闭", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}
