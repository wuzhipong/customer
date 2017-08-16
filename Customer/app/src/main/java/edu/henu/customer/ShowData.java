package edu.henu.customer;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lenovo on 2017/8/12.
 */

public class ShowData extends AppCompatActivity {
    private Database db;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_show);
        db=new Database(this,"Customer.db",null,2);
        final SQLiteDatabase sqlDB = db.getWritableDatabase();
        //查询数据
        Cursor cursor = sqlDB.query("Customer", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                //遍历数据
                final String name = cursor.getString(cursor.getColumnIndex("name"));
                final String phone = cursor.getString(cursor.getColumnIndex("phone"));
                int num = cursor.getInt(cursor.getColumnIndex("num"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String comment=cursor.getString(cursor.getColumnIndex("comment"));
                TextView reasult = (TextView) findViewById(R.id.customer_data);
                String text = "客户姓名:" + name + "\n";
                text += "客户电话:" + phone + " "+"\n";
                text += "产品数量:" + num + "\n";
                text += "产品类型:" + type + "\n";
                text += "备        注:"+ comment + "\n";
                text += "上次修改时间:" + time +" "+ "\n";
                text += " 华丽丽滴----------分割线  "+"\n\n";
               // reasult.setText(text);
                reasult.append(text);
               // reasult.setAutoLinkMask(Linkify.ALL);
                reasult.setMovementMethod(new ScrollingMovementMethod());
            } while (cursor.moveToNext());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(ShowData.this, MainActivity.class);
        startActivity(intent);
    }
}
