package edu.henu.customer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by lenovo on 2017/8/8.
 */

public class Database extends SQLiteOpenHelper {
    public static final String CREATE_CUSTOMER = "create table Customer("
            +"id integer primary key autoincrement,"
            +"name text,"
            +"phone text,"
            +"num integer,"
            +"comment text,"
            +"type text,"
            +"time text)";
    private Context mContext;
    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CUSTOMER);
        Toast.makeText(mContext,"数据库创建成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       try{
           db.execSQL("ALTER TABLE Customer ADD comment TEXT");
           db.execSQL("update Customer set comment =?",new String[]{"空"});
       }catch (Exception e){}

    }
}
