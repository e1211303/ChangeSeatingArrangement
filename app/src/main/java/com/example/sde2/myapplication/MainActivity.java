package com.example.sde2.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /** クリック連打制御時間(ミリ秒) */
    private static final long CLICK_DELAY = 1000;
    /** 前回のクリックイベント実行時間 */
    private static long mOldClickTime;

    /**
     * クリックイベントが実行可能か判断する。
     * @return クリックイベントの実行可否 (true:可, false:否)
     */
    public static boolean isClickEvent() {
        // 現在時間を取得する
        long time = System.currentTimeMillis();

        // 一定時間経過していなければクリックイベント実行不可
        if (time - mOldClickTime < CLICK_DELAY) {
            return false;
        }

        // 一定時間経過したらクリックイベント実行可能
        mOldClickTime = time;
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ようこそのもじれつをセット
        ((TextView)findViewById(R.id.textView_welcome)).setText(
                Html.fromHtml(getString(R.string.Welcome_message))
        );

        //OnClickListener(このアクティビティ)をセット
        Button button_begin=(Button)findViewById(R.id.button_begin);
        Button button_continue = (Button)findViewById(R.id.button_continue);
        button_begin.setOnClickListener(this);
        button_continue.setOnClickListener(this);
    }

    //何かクリックされた
    public void onClick(View view){
        if(!isClickEvent())return;

        ((Button)findViewById(R.id.button_continue)).setClickable(false);
        int id = view.getId();
        switch (id){
            case R.id.button_begin: //始めるボタン
                Intent intent =new Intent(getApplication(),RowAndColumnActivity.class);
                startActivity(intent);
                break;
        }

    }
}
