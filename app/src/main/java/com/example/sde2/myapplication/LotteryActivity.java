package com.example.sde2.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LotteryActivity extends AppCompatActivity {

    private long mGridID = -1;
    private final String KEY_GRID_ID = "GridID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);

        mGridID = getIntent().getLongExtra(KEY_GRID_ID,-1);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        //GridIDを保存しておく
        savedInstanceState.putLong(KEY_GRID_ID,mGridID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        //GridIDを復元する
        String key_GridID = getResources().getString(R.string.IntentExtra_GridID);
        mGridID = savedInstanceState.getLong(key_GridID);
    }

}
