package edu.henu.customer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lenovo on 2017/8/8.
 */

public class SearchActivity extends AppCompatActivity {
    private Database db;//数据库
    private String tag;//查询条件
    private EditText editText;
    private Button queryBtn;
    private String del_phone;
    private Button to_del;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

         //设定返回按钮
        Button backbtn = (Button) findViewById(R.id.search_back);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(SearchActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        db=new Database(this,"Customer.db",null,2);
        queryBtn= (Button) findViewById(R.id.seaBtn);
        queryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(SearchActivity.this,"被点击了",Toast.LENGTH_LONG).show();
                editText= (EditText) findViewById(R.id.selectTag);
                tag=editText.getText().toString();
                queryBtn.setFocusable(true);
                queryBtn.setFocusableInTouchMode(true);
                queryBtn.requestFocus();
                queryBtn.requestFocusFromTouch();
                //隐藏输入法
                InputMethodManager imm =(InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                final SQLiteDatabase sqlDB= db.getWritableDatabase();
                //查询数据
                Cursor cursor = sqlDB.query("Customer", null, "phone=?", new String[]{tag}, null, null, null);
                if (cursor.moveToFirst()){
                    do{
                        //遍历数据
                        final String name = cursor.getString(cursor.getColumnIndex("name"));
                        final String phone= cursor.getString(cursor.getColumnIndex("phone"));
                        int num = cursor.getInt(cursor.getColumnIndex("num"));
                        String type =cursor.getString(cursor.getColumnIndex("type"));
                        String time =cursor.getString(cursor.getColumnIndex("time"));
                        String comment= cursor.getString(cursor.getColumnIndex("comment"));
                        TextView reasult = (TextView) findViewById(R.id.result);
                        String text = "客户姓名:" +name+ "\n";
                        text += "客户电话:" +phone+ "\n";
                        text += "产品数量:" +num+ "\n";
                        text += "产品类型:" +type+ "\n";
                        text += "备        注:" +comment+ "\n";
                        text += "上次修改时间:" +time+ "\n";

                        reasult.setText(text);
                        del_phone = phone;
                        reasult.setAutoLinkMask(Linkify.ALL);
                        Button toChange = (Button) findViewById(R.id.to_change);
                        to_del = (Button) findViewById(R.id.to_del);
                        to_del.setVisibility(View.VISIBLE);
                        toChange.setVisibility(View.VISIBLE);
                        toChange.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent =new Intent(SearchActivity.this,ChangeActivity.class);
                                intent.putExtra("phone",phone);
                                startActivity(intent);
                            }
                        });
                        to_del.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sqlDB.delete("Customer"," phone = ?",new String[]{del_phone});
                                DeleCustomer.deleteFile("data/data/edu.henu.customer/files/Customer.doc");
                                //上传数据前 清空原有文件
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Cursor cursor = sqlDB.query("Customer", null, null, null, null, null, null);
                                        if (cursor.moveToFirst()){
                                            do{
                                                //遍历数据
                                                final String name = cursor.getString(cursor.getColumnIndex("name"));
                                                final String phone= cursor.getString(cursor.getColumnIndex("phone"));
                                                int num = cursor.getInt(cursor.getColumnIndex("num"));
                                                String type =cursor.getString(cursor.getColumnIndex("type"));
                                                String time =cursor.getString(cursor.getColumnIndex("time"));

                                                String text = "客户姓名:" +name+ "\n";
                                                text += "客户电话:" +phone+ "\n";
                                                text += "产品数量:" +num+ "\n";
                                                text += "产品类型:" +type+ "\n";
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
                                Intent startIntent = new Intent(SearchActivity.this,UploadService.class);
                                startService(startIntent);//启动服务
                                AlertDialog.Builder dialog = new AlertDialog.Builder(SearchActivity.this);
                                dialog.setTitle("提示");
                                dialog.setMessage("该用户成功删除！");
                                dialog.setCancelable(true);
                                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                dialog.show();
                            }
                        });
                    }while (cursor.moveToNext());
                }else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(SearchActivity.this);
                    dialog.setTitle("错误提示");
                    dialog.setMessage("该用户不存在，请核对！");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(SearchActivity.this,SearchActivity.class);
                            startActivity(intent);
                        }
                    });
                    dialog.show();
                }
                cursor.close();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
