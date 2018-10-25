package com.example.sde2.myapplication;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Spinner;




public class RowAndColumnActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String ARG_NUM_ROWS = "param1";
    private static final String ARG_NUM_COLS = "param2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_row_and_column);

        ((Button)findViewById(R.id.button_next)).setOnClickListener(this);

    }

    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.button_next: //「次へ」ボタン
                //入力された行数列数
                int rows = (int)((Spinner)findViewById(R.id.spinner_row)).getSelectedItem();
                int cols = (int)((Spinner)findViewById(R.id.spinner_column)).getSelectedItem();

                //フラグメントに渡す引数を用意
                Bundle bundle = new Bundle();
                bundle.putInt(ARG_NUM_ROWS,rows);
                bundle.putInt(ARG_NUM_COLS,cols);

                //フラグメント生成
                SeatGridFragment fragment = new SeatGridFragment();
                fragment.setArguments(bundle);

                //生成したフラグメントをRelativeLayoutに追加
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.GridContainer,fragment);
                transaction.commit();

                break;
        }
    }

}
