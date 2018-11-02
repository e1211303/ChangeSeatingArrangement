package com.example.sde2.myapplication;


import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


public class RowAndColumnActivity extends AppCompatActivity
        implements View.OnClickListener,SeatGridFragment.OnFragmentInteractionListener {

    private static final String ARG_NUM_ROWS = "param1";
    private static final String ARG_NUM_COLS = "param2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_row_and_column);

        ((Button)findViewById(R.id.button_OK)).setOnClickListener(this);
        ((Button)findViewById(R.id.button_next)).setEnabled(false);

        //行数表示部の設定
        LockableScrollView VerticalScroll =
                (LockableScrollView)findViewById(R.id.ScrollView_ForRowNum);
        VerticalScroll.setScrollingEnabled(false);
        VerticalScroll.setVerticalScrollBarEnabled(false);

        //列数表示部の設定
        LockableHorizontalScrollView HorizontalScroll =
                (LockableHorizontalScrollView)findViewById(R.id.Horizontal_ForColNum);
        HorizontalScroll.setScrollingEnabled(false);
        HorizontalScroll.setHorizontalScrollBarEnabled(false);
    }
//todo: じかんがかかるのでなんか表示したい
    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.button_OK: //「決定」ボタン
                //spinner自体を取得
                Spinner spinnerRow = (Spinner)findViewById(R.id.spinner_row);
                Spinner spinnerCol = (Spinner)findViewById(R.id.spinner_column);

                //選択項目を取得（String）
                String stringSelectedRow = spinnerRow.getSelectedItem().toString();
                String stringSelectedCol = spinnerCol.getSelectedItem().toString();
                //数字に変換
                int rows = Integer.parseInt(stringSelectedRow);
                int cols = Integer.parseInt(stringSelectedCol);

                //フラグメントに渡す引数を用意
                Bundle bundle = new Bundle();
                bundle.putInt(ARG_NUM_ROWS,rows);
                bundle.putInt(ARG_NUM_COLS,cols);

                //フラグメント生成
                SeatGridFragment fragment = new SeatGridFragment();
                fragment.setArguments(bundle);

                //生成したフラグメントをRelativeLayoutに追加
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.GridContainer,fragment)
                        .commit();

                //行列番号表示部
                int size = (int)(MyConverterForDpAndPx.convertDp2Px(
                        getResources().getInteger(R.integer.GridSize),
                        this)
                        + 0.5);
                size+=(int)(MyConverterForDpAndPx.convertDp2Px(
                        getResources().getInteger(R.integer.GridMargin),
                        this)
                        + 0.5)*2;
                //列について
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size,size);
                params.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
                LinearLayout linear_col = (LinearLayout)findViewById(R.id.LinearLayout_colNums);
                linear_col.removeAllViews();
                for(int i=0;i<cols;i++)
                {
                    //追加するテキストview
                    TextView textView = new TextView(this);
                    textView.setText(String.valueOf(i+1));
                    textView.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
                    linear_col.addView(textView,params);
                }

                //行番号も
                params= new LinearLayout.LayoutParams(size,size);
                params.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
                LinearLayout linear_row = (LinearLayout)findViewById(R.id.LinearLayout_rowNums);
                linear_row.removeAllViews();
                for(int i=0;i<rows;i++)
                {
                    //追加するテキスト
                    TextView textView = new TextView(this);
                    textView.setText(String.valueOf(i+1));
                    textView.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
                    linear_row.addView(textView,params);
                }

                //次へボタンを有効化
                Button button_next = (Button)findViewById(R.id.button_next);
                button_next.setEnabled(true);




                //チェックボックス操作を促すメッセージ

                break;

            case R.id.button_next:
                //チェックボックス内容を入力


                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
