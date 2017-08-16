package edu.henu.customer;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.cos.COSClientConfig;
import edu.henu.customer.PhoneActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import edu.henu.customer.NameActivity;

/**
 * Created by lenovo on 2017/8/8.
 */

public class AddActivity extends AppCompatActivity {
    private Database dbHelper;//数据库
    private Spinner spinner;//产品类型Spinner
    private String name;//客户姓名
    private String phone;//客户电话
    private int num;//客户剩余产品数量
    private String type;//客户产品类型
    private String time;//时间
    private Button addC;//添加客户按钮
    private String findname;//数据库已有姓名电话
    private String findphone;
    private String comment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_layout);
        dbHelper = new Database(this, "Customer.db", null, 2);
        //根据id获取对象
        spinner = (Spinner) findViewById(R.id.type);
        //显示的数组
        final String arr[] = new String[]{
                "麦丽开",
                "尼格尔"
        };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.my_spinner, arr);
        spinner.setAdapter(arrayAdapter);
        //获取时间
        final java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        final Date curDate = new Date(System.currentTimeMillis());
        time = format.format(curDate);

        addC = (Button) findViewById(R.id.addC);
        addC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editName = (EditText) findViewById(R.id.addname);
                EditText editTel = (EditText) findViewById(R.id.addtel);
                EditText editNum = (EditText) findViewById(R.id.addnum);
                EditText editComment= (EditText) findViewById(R.id.addcomment);
                name = editName.getText().toString();
                phone = editTel.getText().toString();
                comment = editComment.getText().toString();
                boolean a = PhoneActivity.PhoneActivity(phone);//验证手机号正确与否
                // Toast.makeText(AddActivity.this,a+"",Toast.LENGTH_SHORT).show();
                boolean b = NameActivity.isLegalName(name);//判断姓名是否合法

                try {
                    num = Integer.parseInt(editNum.getText().toString());
                } catch (Exception e) {
                    num = -1 ;
                }
                type = spinner.getSelectedItem().toString();
                if (b == false) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(AddActivity.this);
                    dialog.setTitle("Wrong Name");
                    dialog.setMessage("姓名不合法！");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
                } else if (a == false) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(AddActivity.this);
                    dialog.setTitle("Wrong Phone");
                    dialog.setMessage("手机号不合法！");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
                } else if(num<0){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(AddActivity.this);
                    dialog.setTitle("Wrong Num");
                    dialog.setMessage("产品数非法！");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
                }else {
                    //下面进行数据库已有字段验证
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    Cursor cursor = db.query("Customer", null, "phone=?", new String[]{phone}, null, null, null);
                    if (cursor.moveToFirst()) {
                        do {
                            //遍历数据
                            findphone = cursor.getString(cursor.getColumnIndex("phone"));
                          //  Toast.makeText(AddActivity.this,findname+findphone+"",Toast.LENGTH_SHORT).show();
                        } while (cursor.moveToNext());
                    }
                    if (findphone != null) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(AddActivity.this);
                        dialog.setTitle("Wrong");
                        dialog.setMessage("用户已经存在！");
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                findphone=null;
                            }
                        });
                        dialog.show();
                    } else {
                        //开始组装数据
                        if (comment.equals("")){
                            comment="空";
                        }
                        values.put("name", name);
                        values.put("phone", phone);
                        values.put("num", num);
                        values.put("type", type);
                        values.put("comment",comment);
                        values.put("time", time);
                        db.insert("Customer", null, values);//插入数据
                        values.clear();
                        DeleCustomer.deleteFile("data/data/edu.henu.customer/files/Customer.doc");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SQLiteDatabase sql = dbHelper.getWritableDatabase();
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
                                        String comment =cursor.getString(cursor.getColumnIndex("comment"));

                                        String text = "客户姓名:" +name+ "\n";
                                        text += "客户电话:" +phone+ "\n";
                                        text += "产品数量:" +num+ "\n";
                                        text += "产品类型:" +type+ "\n";
                                        text += "上次修改时间:" +time+ "\n";
                                        text +=  "备注"+comment+"\n";
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
                        Intent startIntent = new Intent(AddActivity.this,UploadService.class);
                        startService(startIntent);//启动服务
                        Toast.makeText(AddActivity.this, "数据添加成功", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(AddActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(AddActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
