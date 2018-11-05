package com.example.sde2.myapplication;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;


public class RowAndColumnActivity extends AppCompatActivity
        implements
        SeatGridFragment.OnFragmentInteractionListener,
        InputRowAndColumnFragment.OnFragmentInteractionListener
{

    private static final String ARG_NUM_ROWS = "param1";
    private static final String ARG_NUM_COLS = "param2";

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
    public void onSetRowsAndColumns(final InputRowAndColumnFragment inputRowAndColumnFragment, final int rows, final int columns){
        //todo 行列数が帰ってきたときの処理
        //todo Gridのフラグメント差し込み

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("処理中");
        progressDialog.setMessage("ちょっとまってね");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

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
                .replace(R.id.RelativeLayout_ForDisplayGrid,seatGridFragment)
                .commit();

        progressDialog.dismiss();
    }

    @Override
    public void onGoToNext(InputRowAndColumnFragment fragment){
        //todo つぎへボタンが押されたとき
    }
}
