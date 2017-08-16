package edu.henu.customer;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by lenovo on 2017/8/8.
 */

public class ChangeActivity extends AppCompatActivity {
    private Spinner spinner;
    private Database db;//数据库
    private String typeMessage;
    private String data;
    private int typenum;//修改后的数量
    private String time;
    private String typecomment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_layout);

        //定义产品类型
        spinner=(Spinner) findViewById(R.id.change_type);
        //显示的数组
        final String arr[]=new String[]{
                "麦丽开",
                "尼格尔"
        };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.my_spinner, arr);
        spinner.setAdapter(arrayAdapter);

        db=new Database(this,"Customer.db",null,2);
        //进入此活动首先判断是不是通过上个活动传递过name信息 如果传递有name信息直接进行查询结果的显示
         Intent getIntent = getIntent();
         data =getIntent.getStringExtra("phone");
       // Toast.makeText(ChangeActivity.this,data,Toast.LENGTH_LONG).show();
                //data为空  需要输入数据
        if (data==null){
            //第一种逻辑 用户通过输入电话或姓名得到信息进行修改
            final Button ok_btn = (Button) findViewById(R.id.ok_btn);
            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //首先获取焦点
                    ok_btn.setFocusable(true);
                    ok_btn.setFocusableInTouchMode(true);
                    ok_btn.requestFocus();
                    ok_btn.requestFocusFromTouch();
                    //获取用户输入信息
                    EditText typeText = (EditText) findViewById(R.id.type_text);
                    typeMessage = typeText.getText().toString();
                    SQLiteDatabase sqlDB = db.getWritableDatabase();
                    //查询数据
                    Cursor cursor = sqlDB.query("Customer", null, "phone=?", new String[]{typeMessage}, null, null, null);

                    if (cursor.moveToFirst()) {
                        do {
                            //遍历数据
                            final int id = cursor.getInt(cursor.getColumnIndex("id"));
                            final String name = cursor.getString(cursor.getColumnIndex("name"));
                            final String phone = cursor.getString(cursor.getColumnIndex("phone"));
                            int num = cursor.getInt(cursor.getColumnIndex("num"));
                            final String type = cursor.getString(cursor.getColumnIndex("type"));
                            final String time = cursor.getString(cursor.getColumnIndex("time"));
                            String comment=cursor.getString(cursor.getColumnIndex("comment"));

                            //如果查询到数据则隐藏最上方输入框 显示结果
                            LinearLayout line1 = (LinearLayout) findViewById(R.id.find_line);
                            LinearLayout line2 = (LinearLayout) findViewById(R.id.change_line);
                            LinearLayout line3 = (LinearLayout) findViewById(R.id.btn_line);
                            line1.setVisibility(View.GONE);
                            line2.setVisibility(View.VISIBLE);
                            line3.setVisibility(View.VISIBLE);

                            //显示查询结果 提供修改功能
                            final TextView Cname = (TextView) findViewById(R.id.change_name);
                            final EditText Cnum = (EditText) findViewById(R.id.change_num);
                            final EditText Cphone = (EditText) findViewById(R.id.change_phone);
                            final EditText Ccomment= (EditText) findViewById(R.id.change_comment);

                            Cname.setText(name);
                            Cphone.setText(phone.toCharArray(), 0, phone.length());
                            Cnum.setText((num + "").toCharArray(), 0, (num + "").length());
                            Ccomment.setText(comment.toCharArray(),0,comment.length());
                            if (type.equals("尼格尔")) {
                                spinner.setSelection(1);
                            } else {
                                spinner.setSelection(0);
                            }

                            //Toast.makeText(ChangeActivity.this,id,Toast.LENGTH_LONG).show();
                            //修改和返回事件
                            final Button btn1 = (Button) findViewById(R.id.btn_change);
                            final Button btn2 = (Button) findViewById(R.id.btn_cancel);
                            btn1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //点击后首先获取焦点
                                    btn1.setFocusable(true);
                                    btn1.setFocusableInTouchMode(true);
                                    btn1.requestFocus();
                                    btn1.requestFocusFromTouch();

                                    //获取修改后的数据 进行保存


                                    SQLiteDatabase sqlup = db.getWritableDatabase();
                                    ContentValues values = new ContentValues();
                                    Cursor cursors = sqlup.query("Customer", null, "phone=? and name!=?", new String[]{Cphone.getText().toString(),Cname.getText().toString()}, null, null, null);
                                    if (cursors.moveToNext()){
                                        do {
                                            final String fphone = cursors.getString(cursors.getColumnIndex("phone"));
                                            if (fphone!=null){
                                                AlertDialog.Builder dialog = new AlertDialog.Builder(ChangeActivity.this);
                                                dialog.setTitle("提示");
                                                dialog.setMessage("此电话已经存在！");
                                                dialog.setCancelable(true);
                                                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //确定后返回主页面

                                                    }
                                                });
                                                dialog.show();
                                            }
                                        }while (cursors.moveToNext());
                                        cursors.close();
                                    }else{
                                        boolean p= PhoneActivity.PhoneActivity(Cphone.getText().toString());
                                        try {
                                            typenum = Integer.parseInt(Cnum.getText().toString());
                                        } catch (Exception e) {
                                            typenum = -1;
                                        }
                                        if (p==false||typenum<0){
                                            AlertDialog.Builder dialog = new AlertDialog.Builder(ChangeActivity.this);
                                            dialog.setTitle("Wrong");
                                            dialog.setMessage("修改信息有误！");
                                            dialog.setCancelable(true);
                                            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            });
                                            dialog.show();
                                        }else{
                                            if (Ccomment.getText().toString().equals("")){
                                                typecomment="空";
                                            }
                                            values.put("phone", Cphone.getText().toString());
                                            values.put("num", Cnum.getText().toString());
                                            values.put("type", spinner.getSelectedItem().toString());
                                            values.put("comment",typecomment);
                                            //获取时间
                                            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
                                            final Date curDate = new Date(System.currentTimeMillis());
                                            String time = format.format(curDate);
                                            values.put("time", time);
                                            //数据组装完毕
                                            //更新数据
                                            sqlup.update("Customer", values, "id=?", new String[]{id+""});
                                            DeleCustomer.deleteFile("data/data/edu.henu.customer/files/Customer.doc");
                                            //上传数据前 清空原有文件
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
                                                            text += "备注:"   +comment+"\n";
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
                                            //启动服务以完成更新
                                            Intent startIntent = new Intent(ChangeActivity.this,UploadService.class);
                                            startService(startIntent);//启动服务
                                            AlertDialog.Builder dialog = new AlertDialog.Builder(ChangeActivity.this);
                                            dialog.setTitle("提示");
                                            dialog.setMessage("数据更新成功！");
                                            dialog.setCancelable(true);
                                            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //确定后返回主页面
                                                    Intent intent = new Intent(ChangeActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                            dialog.show();
                                        }
                                    }


                                }
                            });
                            btn2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //点击后首先获取焦点
                                    btn1.setFocusable(true);
                                    btn1.setFocusableInTouchMode(true);
                                    btn1.requestFocus();
                                    btn1.requestFocusFromTouch();

                                    //取消后返回主页面
                                    Intent intent = new Intent(ChangeActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                        } while (cursor.moveToNext());
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ChangeActivity.this);
                        dialog.setTitle("错误提示");
                        dialog.setMessage("该用户不存在，请核对！");
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ChangeActivity.this, ChangeActivity.class);
                                startActivity(intent);
                            }
                        });
                        dialog.show();
                    }
                    cursor.close();
                }
            });
        }else {
            typeMessage = data;
            SQLiteDatabase sqlDB = db.getWritableDatabase();
            //查询数据
            Cursor cursor = sqlDB.query("Customer", null, "phone=?", new String[]{typeMessage}, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    //遍历数据
                    final int id = cursor.getInt(cursor.getColumnIndex("id"));
                    final String name = cursor.getString(cursor.getColumnIndex("name"));
                    final String phone = cursor.getString(cursor.getColumnIndex("phone"));
                    int num = cursor.getInt(cursor.getColumnIndex("num"));
                    String type = cursor.getString(cursor.getColumnIndex("type"));
                    final String time = cursor.getString(cursor.getColumnIndex("time"));
                    String comment = cursor.getString(cursor.getColumnIndex("comment"));

                    //如果查询到数据则隐藏最上方输入框 显示结果
                    LinearLayout line1 = (LinearLayout) findViewById(R.id.find_line);
                    LinearLayout line2 = (LinearLayout) findViewById(R.id.change_line);
                    LinearLayout line3 = (LinearLayout) findViewById(R.id.btn_line);
                    line1.setVisibility(View.GONE);
                    line2.setVisibility(View.VISIBLE);
                    line3.setVisibility(View.VISIBLE);

                    //显示查询结果 提供修改功能
                    final TextView Cname = (TextView) findViewById(R.id.change_name);
                    final EditText Cnum = (EditText) findViewById(R.id.change_num);
                    final EditText Cphone = (EditText) findViewById(R.id.change_phone);
                    final EditText Ccomment = (EditText) findViewById(R.id.change_comment);
                    if (comment==null){
                        comment = "空";
                    }
                    Cname.setText(name);
                    Cphone.setText(phone.toCharArray(), 0, phone.length());
                    Cnum.setText((num + "").toCharArray(), 0, (num + "").length());
                    Ccomment.setText(comment.toCharArray(),0,comment.length());
                    if (type.equals("尼格尔")) {
                        spinner.setSelection(1);
                    } else {
                        spinner.setSelection(0);
                    }

                    final Button btn1 = (Button) findViewById(R.id.btn_change);
                    final Button btn2 = (Button) findViewById(R.id.btn_cancel);
                    btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //点击后首先获取焦点
                            btn1.setFocusable(true);
                            btn1.setFocusableInTouchMode(true);
                            btn1.requestFocus();
                            btn1.requestFocusFromTouch();

                            //获取修改后的数据 进行保存

                            SQLiteDatabase sqlup = db.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            boolean a= PhoneActivity.PhoneActivity(Cphone.getText().toString());
                            Cursor cursors = sqlup.query("Customer", null, "phone=? and name != ?", new String[]{Cphone.getText().toString(),Cname.getText().toString()}, null, null, null);
                            if (cursors.moveToNext()){
                                do {
                                    final String fphone = cursors.getString(cursors.getColumnIndex("phone"));
                                    if (fphone!=null){
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(ChangeActivity.this);
                                        dialog.setTitle("提示");
                                        dialog.setMessage("此电话已经存在！");
                                        dialog.setCancelable(true);
                                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //确定后返回主页面

                                            }
                                        });
                                        dialog.show();
                                    }
                                }while (cursors.moveToNext());
                                cursors.close();
                            } else if(a==false){
                                AlertDialog.Builder dialog = new AlertDialog.Builder(ChangeActivity.this);
                                dialog.setTitle("提示");
                                dialog.setMessage("电话号不合法！");
                                dialog.setCancelable(true);
                                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //确定后返回主页面

                                    }
                                });
                                dialog.show();
                            }else{
                                if (Ccomment.getText().toString().equals("")){
                                    typecomment="空";
                                }
                                values.put("phone", Cphone.getText().toString());
                                values.put("num", Cnum.getText().toString());
                                values.put("type", spinner.getSelectedItem().toString());
                                values.put("comment",typecomment);
                                //获取时间
                                java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
                                final Date curDate = new Date(System.currentTimeMillis());
                                String time = format.format(curDate);
                                values.put("time", time);
                                //数据组装完毕
                                //更新数据
                                sqlup.update("Customer", values, "id=?", new String[]{id+""});
                                DeleCustomer.deleteFile("data/data/edu.henu.customer/files/Customer.doc");
                                //上传数据前 清空原有文件
                                //新建线程以写入文件
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
                                                String comment =cursor.getString(cursor.getColumnIndex("comment"));
                                                String text = "客户姓名:" +name+ "\n";
                                                text += "客户电话:" +phone+ "\n";
                                                text += "产品数量:" +num+ "\n";
                                                text += "产品类型:" +type+ "\n";
                                                text += "备注"+    comment   +"\n";
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
                                Intent startIntent = new Intent(ChangeActivity.this,UploadService.class);
                                startService(startIntent);//启动服务
                                AlertDialog.Builder dialog = new AlertDialog.Builder(ChangeActivity.this);
                                dialog.setTitle("提示");
                                dialog.setMessage("数据更新成功！");
                                dialog.setCancelable(true);
                                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //确定后返回主页面
                                        Intent intent = new Intent(ChangeActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                dialog.show();
                            }
                        }
                    });
                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //点击后首先获取焦点
                            btn1.setFocusable(true);
                            btn1.setFocusableInTouchMode(true);
                            btn1.requestFocus();
                            btn1.requestFocusFromTouch();

                            //取消后返回主页面
                            Intent intent = new Intent(ChangeActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                } while (cursor.moveToNext());
            }
        }
    }
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(ChangeActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
