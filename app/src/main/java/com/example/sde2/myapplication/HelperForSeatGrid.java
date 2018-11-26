package com.example.sde2.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//todo データベースようのヘルパーを作成

public class HelperForSeatGrid extends SQLiteOpenHelper {
    private static final int DB_Version = 1;
    //クエリ文
    private static String CreateTable_SeatGrid;
    private static String CreateTable_SeatState;

    private static String DropTable_SeatGrid;
    private static String DropTable_SeatState;


    public HelperForSeatGrid(Context context){
        super(context,
                context.getResources().getString(R.string.DB_FileName),
                null,
                context.getResources().getInteger(R.integer.DB_Version));

        //クエリ文取得しておく
        CreateTable_SeatGrid =
                context.getResources().getString(R.string.DB_Query_CreateTable_SeatGrid);
        CreateTable_SeatState =
                context.getResources().getString(R.string.DB_Query_CreateTable_SeatState);

        DropTable_SeatGrid =
                context.getResources().getString(R.string.DB_Query_DropTable_SeatGrid);
        DropTable_SeatState =
                context.getResources().getString(R.string.DB_Query_DropTable_SeatState);
    }

    public void onCreate(SQLiteDatabase db)
    {
        //todo テーブル全部作る
        db.execSQL(CreateTable_SeatGrid);
        db.execSQL(CreateTable_SeatState);
    }

    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL(DropTable_SeatGrid);
        db.execSQL(DropTable_SeatState);
        onCreate(db);
    }
}
