package com.example.sde2.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final int REQUEST_CODE_SEATGRID = 11;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ようこそのもじれつをセット
        ((TextView)findViewById(R.id.TextView_welcome)).setText(
                Html.fromHtml(getString(R.string.Welcome_message))
        );

        //OnClickListener(このアクティビティ)をセット
        Button button_begin=(Button)findViewById(R.id.Button_begin);
        Button button_continue = (Button)findViewById(R.id.Button_continue);
        button_begin.setOnClickListener(this);
        button_continue.setOnClickListener(this);
    }

    //何かクリックされた
    public void onClick(View view){
        MyUtil_ForButton.disableButtonForMillisecs((Button)view,1000);
        int id = view.getId();
        switch (id){
            case R.id.Button_begin: //始めるボタン
                Intent intent =new Intent(getApplication(),RowAndColumnActivity.class);
                startActivityForResult(intent,REQUEST_CODE_SEATGRID);
                break;

            case R.id.Button_continue:
                //todo 保存済みのGridを選ばせるアクティビティ
        }

    }

    //結果受け取り

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch(requestCode){
            case(REQUEST_CODE_SEATGRID):
                if(resultCode==RESULT_OK){
                    long GridID =
                            data.getLongExtra(
                                    getResources().getString(R.string.IntentExtra_GridID),
                                    -1);
                    if(GridID != -1){
                        //くじ引きのアクティビティ
                        Intent intent = new Intent(getApplicationContext(),LotteryActivity.class);
                        intent.putExtra(
                                getResources().getString(R.string.IntentExtra_GridID), GridID);

                        startActivity(intent);
                    }else{
                        //ID受け取り失敗
                        new AlertDialog.Builder(this)
                                .setTitle("ID取得に失敗")
                                .show();
                    }
                }
                //あとはとりあえず何もしない
        }
    }
}
