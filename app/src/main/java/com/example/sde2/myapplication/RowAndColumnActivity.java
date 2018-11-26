package com.example.sde2.myapplication;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class RowAndColumnActivity extends AppCompatActivity
        implements
        SeatGridFragment.OnFragmentInteractionListener,
        InputRowAndColumnFragment.OnFragmentInteractionListener,
        InputUsingScopeSettingFragment.OnFragmentInteractionListener
{

    private static final String ARG_NUM_ROWS = "param1";
    private static final String ARG_NUM_COLS = "param2";

    private static final String TAG_SEAT_GRID = "SeatGridFragment";
    private static final String TAG_SCOPE ="ScopeFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_row_and_column);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.RelativeLayout_ForInputPrompt,new InputRowAndColumnFragment())
                .commit();
    }


    @Override
    public void onSetRowsAndColumns(final InputRowAndColumnFragment inputRowAndColumnFragment, final int rows, final int columns)
    {
        //引数をまとめたもの
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_NUM_ROWS,rows);
        bundle.putInt(ARG_NUM_COLS,columns);

        //フラグメントの実体を生成
        SeatGridFragment seatGridFragment = new SeatGridFragment();
        seatGridFragment.setArguments(bundle);

        //フラグメント差し込み
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.RelativeLayout_For_Display_Bottom,seatGridFragment,TAG_SEAT_GRID)
                .commit();
    }

    //todo 前のフラグメントの状態保持？
    //行列入力部の次へで
    @Override
    public void onGoToNext(InputRowAndColumnFragment fragment)
    {
        //下部のグリッドを取得できるか
        Fragment fragmentBottom=
                getSupportFragmentManager()
                        .findFragmentByTag(TAG_SEAT_GRID);
        if(!(fragmentBottom instanceof SeatGridFragment))
            return; //何もせず

        SeatGridFragment seatGridFragment = (SeatGridFragment)fragmentBottom;

        //入力部fragment差し替え
        InputUsingScopeSettingFragment inputUsingScopeSettingFragment=
                new InputUsingScopeSettingFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.RelativeLayout_ForInputPrompt,inputUsingScopeSettingFragment,TAG_SCOPE)
                .commit();

        //空席状態を取得
        Boolean[][] seatState =
                seatGridFragment.getIsCheckedAll();
        //falseのところをnullに
        final int rows = seatState.length;
        final int cols = seatState[0].length;
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                Boolean b = seatState[i][j];
                if(b != null && b.booleanValue() == false){
                    seatState[i][j] = null;
                }
            }
        }

        //空席部分以外のGridを表示
        seatGridFragment.prepareSeatGrid(rows,cols,seatState,null);
        //チェックを外す
        seatGridFragment.setIsCheckedAll(false);


    }

    //スコープ設定の次へで
    @Override
    public void onGoToNext(InputUsingScopeSettingFragment fragment){
        //下部のグリッドを取得できるか
        Fragment fragmentBottom=
                getSupportFragmentManager()
                        .findFragmentByTag(TAG_SEAT_GRID);
        if(!(fragmentBottom instanceof SeatGridFragment))
            return; //何もせず

        SeatGridFragment seatGridFragment = (SeatGridFragment)fragmentBottom;

        //席状態取得
        Boolean[][] seatStates =
                seatGridFragment.getIsCheckedAll();

        final int rows = seatStates.length;
        final int cols = seatStates[0].length;

        //todo Gridの名前を聞いてDBに保存
//
//        //DB準備
//        MySQLiteOpenHelperForSeatGrid helperForSeatGrid =
//                new MySQLiteOpenHelperForSeatGrid(getApplicationContext());
//        //DBインスタンス取得
//        SQLiteDatabase DB_SeatGrid = helperForSeatGrid.getWritableDatabase();
    }

    //DB操作用
    private HelperForSeatGrid mHelperForSeatGrid;
    private SQLiteDatabase db;
    private final String TableName_SeatGrid =
            getResources().getString(R.string.DB_TableName_SeatGrid);
    private final String TableName_SeatState =
            getResources().getString(R.string.DB_TableName_SeatState);

    private void setDataBase()
    {
        mHelperForSeatGrid = new HelperForSeatGrid(getApplicationContext());
        db = mHelperForSeatGrid.getWritableDatabase();
    }

    //座席状態で保存。いつかは人名入り
    private boolean addSeatGrid(Boolean[][] seatStates,String GridName){
        if(db==null) return false;
        if(seatStates==null) return false;

        //SeatGrid分
        ContentValues values_SeatGrid = new ContentValues();
        values_SeatGrid.put(getResources().getString(R.string.DB_ColName_SeatGrid_Name),GridName);
        values_SeatGrid.put(getResources().getString(R.string.DB_ColName_SeatGrid_Rows),seatStates.length);
        values_SeatGrid.put(getResources().getString(R.string.DB_ColName_SeatGrid_Cols),seatStates[0].length);
        long ret = db.insert(TableName_SeatGrid,null,values_SeatGrid);
        if(ret == -1)return false;

        //SeatState分
        for(Boolean[] bools : seatStates){
            for(Boolean b : bools){
                // todo 上で取得したGridに紐づけながら追加
            }
        }

    }
}
