package edu.henu.customer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //下边用于创建数据库
        db = new Database(this,"Customer.db",null,2);
        db.getWritableDatabase();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_button:
                Intent intent1 =new Intent("edu.henu.customer.SearchActivity");
                startActivity(intent1);
                break;
            case R.id.add_button:
                Intent intent2 =new Intent("edu.henu.customer.AddActivity");
                startActivity(intent2);
                break;
            case R.id.del_button:
                Intent intent3 =new Intent("edu.henu.customer.DelActivity");
                startActivity(intent3);
                break;
            case R.id.change_button:
                Intent intent4 =new Intent("edu.henu.customer.ChangeActivity");
                startActivity(intent4);
                break;
            case R.id.upbutton:
             //  Toast.makeText(MainActivity.this,"this is clicked",Toast.LENGTH_SHORT).show();
                Intent intent5 =new Intent("edu.henu.customer.ShowData");
                startActivity(intent5);
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}


