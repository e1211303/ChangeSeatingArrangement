package com.example.sde2.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ようこそのもじれつをセット
        ((TextView)findViewById(R.id.textView_welcome)).setText(
                Html.fromHtml(getString(R.string.Welcome_message))
        );
    }

    //何かクリックされた
    public void onClick(View view){
        switch (view.getId()){
            case R.id.button_begin: //始めるボタン
                Intent intent = new Intent(getApplication(),RowAndColumnActivity.class);
                startActivity(intent);
                break;
        }
    }
}
