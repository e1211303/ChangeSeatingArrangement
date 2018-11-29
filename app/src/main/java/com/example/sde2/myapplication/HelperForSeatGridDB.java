package com.example.sde2.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FilenameFilter;

//todo データベースようのヘルパーを作成

public class HelperForSeatGridDB extends SQLiteOpenHelper {
    private static final int DB_Version = 1;
    private static final String FileName ="SeatGrid.db";
    //定数
    public class SeatGridConstants{
        private SeatGridConstants(){}

        public static final String TableName = "SEATGRID_TABLE";

        public static final String ColName_ID = "_ID";
        public static final String ColName_Name ="GRID_NAME";
        public static final String ColName_Rows="ROWS";
        public static final String ColName_Cols="COLS";

        public static final String Query_CreateTable =
                "CREATE TABLE " + TableName + "("+
                        ColName_ID + " INTEGER PRIMARY KEY," +
                        ColName_Name + " TEXT NOT NULL," +
                        ColName_Rows + " INTEGER," +
                        ColName_Cols + " INTEGER"+
                        ");";

        public  static final String Query_DropTable = "DROP TABLE " + TableName + ";";
    }

    public class SeatStateConstants{
        private SeatStateConstants(){}

        public static final String TableName = "SEATSTATE_TABLE";

        public static final String ColName_ID = "_ID";
        public static final String ColName_Row = "ROW_NUM";
        public static final String ColName_Col = "COL_NUM";
        public static final String ColName_isEnabled = "IS_ENABLED";
        public static final String ColName_isScoped ="IS_SCOPED";
        public static final String ColName_isEmpty = "IS_EMPTY";
        public static final String ColName_StudentID = "STUDENT_ID";

        public static final String Query_CreateTable =
                "CREATE TABLE " + TableName +"("+
                        ColName_ID + " INTEGER," +
                        ColName_Row + " INTEGER," +
                        ColName_Col + " INTEGER," +
                        ColName_isEmpty + " BOOLEAN," +
                        ColName_isEnabled + " BOOLEAN,"+
                        ColName_isScoped + " BOOLEAN," +
                        ColName_StudentID + " TEXT," +
                        "PRIMARY KEY ("+ (ColName_ID+ColName_Row+ColName_Col) +")"+
                        ");";

        public static final String Query_DropTable = "DROP TABLE "+TableName+";";
    }


    public HelperForSeatGridDB(Context context){
        super(context,FileName, null, DB_Version);
    }

    public void onCreate(SQLiteDatabase db)
    {
        //todo テーブル全部作る
        db.execSQL(SeatGridConstants.Query_CreateTable);
        db.execSQL(SeatStateConstants.Query_CreateTable);

    }

    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL(SeatGridConstants.Query_DropTable);
        db.execSQL(SeatStateConstants.Query_DropTable);
        onCreate(db);
    }
}
