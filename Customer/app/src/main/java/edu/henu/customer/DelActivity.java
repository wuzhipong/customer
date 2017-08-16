package edu.henu.customer;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lenovo on 2017/8/8.
 */

public class DelActivity extends AppCompatActivity{
    private Database db;
    private String data;
    private String findname;
    private String findphone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.del_layout);

        db = new Database(this,"Customer.db",null,2);

        final Button btnDel = (Button) findViewById(R.id.del_btn);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //按钮获取焦点
                btnDel.setFocusable(true);
                btnDel.setFocusableInTouchMode(true);
                btnDel.requestFocus();
                btnDel.requestFocusFromTouch();

                //获取用户输入内容
                EditText del_type = (EditText) findViewById(R.id.del_type);
                data = del_type.getText().toString();

                SQLiteDatabase sqlDEL = db.getWritableDatabase();

                SQLiteDatabase dbQ = db.getWritableDatabase();
                ContentValues values = new ContentValues();
                Cursor cursor = dbQ.query("Customer", null, "phone=?", new String[]{data}, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        //遍历数据
                        //findname = cursor.getString(cursor.getColumnIndex("name"));
                        findphone = cursor.getString(cursor.getColumnIndex("phone"));
                    } while (cursor.moveToNext());
                }
                if (findphone == null) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DelActivity.this);
                    dialog.setTitle("Wrong");
                    dialog.setMessage("用户不存在！");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(DelActivity.this, DelActivity.class);
                            startActivity(intent);
                        }
                    });
                    dialog.show();
                }else {
                    sqlDEL.delete("Customer","phone = ?",new String[]{data});
                    DeleCustomer.deleteFile("data/data/edu.henu.customer/files/Customer.doc");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SQLiteDatabase sql = db.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            Cursor cursor = sql.query("Customer", null, null, null, null, null, null);
                            if (cursor.moveToFirst()){
                                do{
                                    //遍历数据
                                    final String name = cursor.getString(cursor.getColumnIndex("name"));
                                    final String phone= cursor.getString(cursor.getColumnIndex("phone"));
                                    int num = cursor.getInt(cursor.getColumnIndex("num"));
                                    String type =cursor.getString(cursor.getColumnIndex("type"));
                                    String time =cursor.getString(cursor.getColumnIndex("time"));
                                    String comment=cursor.getString(cursor.getColumnIndex("comment"));

                                    String text = "客户姓名:" +name+ "\n";
                                    text += "客户电话:" +phone+ "\n";
                                    text += "产品数量:" +num+ "\n";
                                    text += "产品类型:" +type+ "\n";
                                    text += "备        注:"+ comment + "\n";
                                    text += "上次修改时间:" +time+ "\n";
                                    // Log.d("UploadService",text);
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

                                }while (cursor.moveToNext());
                            }
                        }
                    }).start();
                    Intent startIntent = new Intent(DelActivity.this,UploadService.class);
                    startService(startIntent);//启动服务

                    //提示dialog
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DelActivity.this);
                    dialog.setTitle("提示");
                    dialog.setMessage("数据删除成功！");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //确定后返回主页面
                            Intent intent = new Intent(DelActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                    dialog.show();
                }
            }
        });
        //点错按钮
        final Button wrongBtn = (Button) findViewById(R.id.wrong_chioce);
        wrongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //按钮获取焦点
                wrongBtn.setFocusable(true);
                wrongBtn.setFocusableInTouchMode(true);
                wrongBtn.requestFocus();
                wrongBtn.requestFocusFromTouch();

                Intent intent = new Intent(DelActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(DelActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
