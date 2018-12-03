package com.example.sde2.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

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
                int requestCode = 11; //適当
                startActivityForResult(intent,requestCode);
                break;
        }

    }
}
