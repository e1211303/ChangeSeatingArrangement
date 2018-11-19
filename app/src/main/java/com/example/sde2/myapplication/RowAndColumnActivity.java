package com.example.sde2.myapplication;


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
        //todo falseのところをnullに

        
        seatGridFragment.prepareSeatGrid(seatState.length,seatState[0].length,seatState,null);



    }

    //スコープ設定の次へで
    @Override
    public void onGoToNext(InputUsingScopeSettingFragment fragment){

    }
}
