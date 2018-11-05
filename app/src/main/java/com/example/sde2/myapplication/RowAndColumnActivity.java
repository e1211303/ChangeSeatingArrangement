package com.example.sde2.myapplication;


import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class RowAndColumnActivity extends AppCompatActivity
        implements
        SeatGridFragment.OnFragmentInteractionListener,
        InputRowAndColumnFragment.OnFragmentInteractionListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_row_and_column);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.relativeLayout_ForInputPrompt,new InputRowAndColumnFragment())
                .commit();
    }





    @Override
    public void onSetRowsAndColumns(InputRowAndColumnFragment fragment,int rows, int columns){
        //todo 行列数が帰ってきたときの処理
        //todo Gridのフラグメント差し込み(別スレッドで)
    }

    @Override
    public void onGoToNext(InputRowAndColumnFragment fragment){
        //todo つぎへボタンが押されたとき
    }
}
