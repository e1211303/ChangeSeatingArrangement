package com.example.sde2.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// データベースようのヘルパー

public class HelperForSeatGridDB extends SQLiteOpenHelper {
    private static final int DB_Version = 4;
    private static final String FileName ="SeatGrid.db";
    //定数
    public class SeatGridConstants{
        private SeatGridConstants(){}

        public static final String TableName = "SEKIGAE_TABLE";

        public static final String ColName_ID = "_ID";
        public static final String ColName_Width ="WIDTH";
        public static final String ColName_Height ="HEIGHT";
        public static final String ColName_Memo = "MEMO";

        public static final String Query_CreateTable =
                "CREATE TABLE " + TableName + "("+
                        ColName_ID + " INTEGER PRIMARY KEY," +
                        ColName_Width + " INTEGER," +
                        ColName_Height + " INTEGER,"+
                        ColName_Memo + " TEXT" +
                        ");";

        public  static final String Query_DropTable = "DROP TABLE " + TableName + ";";
    }

    public class SeatStateConstants{
        private SeatStateConstants(){}

        public static final String TableName = "SEATS_TABLE";

        public static final String ColName_ID = "_ID";
        public static final String ColName_Pos = "POS";
        public static final String ColName_Scope = "SCOPE";
        public static final String ColName_StudentName = "NAME";

        public static final int Scope_Normal = 0;
        public static final int Scope_Scoped = 1;
        public static final int Scope_Empty = 2;

        public static final String Query_CreateTable =
                "CREATE TABLE " + TableName +"("+
                        ColName_ID + " INTEGER," +
                        ColName_Pos + " INTEGER," +
                        ColName_Scope + " INTEGER," +
                        ColName_StudentName + " TEXT," +
                        "PRIMARY KEY (" + ColName_ID + "," + ColName_Pos + "),"+
                        "FOREIGN KEY(" + ColName_ID + ")" + " REFERENCES " + SeatGridConstants.TableName +"("+ SeatGridConstants.ColName_ID +")"+
                        ");";

        public static final String Query_DropTable = "DROP TABLE "+TableName+";";
    }

    //todo 学生のidと名前のデータベースも

    //外部キー有効化
    private final String Query_EnableForeignKey="PRAGMA foreign_keys=true;";

    public HelperForSeatGridDB(Context context){
        super(context,FileName, null, DB_Version);
    }

    public void onCreate(SQLiteDatabase db)
    {
        //todo テーブル全部作る
        db.execSQL(SeatGridConstants.Query_CreateTable);
        db.execSQL(SeatStateConstants.Query_CreateTable);
        db.execSQL(Query_EnableForeignKey);
    }

    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL(SeatStateConstants.Query_DropTable);
        db.execSQL(SeatGridConstants.Query_DropTable);
        onCreate(db);
    }
}
