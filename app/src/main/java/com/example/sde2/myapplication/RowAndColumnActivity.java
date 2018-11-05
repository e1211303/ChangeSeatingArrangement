package com.example.sde2.myapplication;


import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class RowAndColumnActivity extends AppCompatActivity
        implements
        View.OnClickListener,
        SeatGridFragment.OnFragmentInteractionListener,
        InputRowAndColumnFragment.OnFragmentInteractionListener,
        ObservableScrollView.ScrollViewListener,
        ObservableHorizontalScrollView.ScrollViewListener
{

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

        //スクロール通知を受け取る設定
        ObservableScrollView observableScrollView=
                findViewById(R.id.ScrollView_ForGrid);
        observableScrollView.setOnScrollViewListener(this);

        ObservableHorizontalScrollView observableHorizontalScrollView=
                (ObservableHorizontalScrollView)findViewById(R.id.HorizontalScrollView_ForGrid);
        observableHorizontalScrollView.setOnScrollViewListener(this);
    }
//todo: FragmentInteractionに移動させる
    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.button_OK: //「決定」ボタン
                //定義しておいた処理をこことは非同期的に行う
                new AsyncTask_ForCheckboxGrid(this).execute();
                break;

            case R.id.button_next:
                //チェックボックス内容を入力
                break;
            default:
                break;
        }
    }

    //グリッド横スクロール
    @Override
    public void onScrollChanged(ObservableHorizontalScrollView scrollView, int x, int y, int oldx, int oldy){
        int id = scrollView.getId();
        switch (id)
        {
            case R.id.HorizontalScrollView_ForGrid:
                //横合わせる
                LockableHorizontalScrollView lockableHorizontalScrollView=
                        (LockableHorizontalScrollView)findViewById(R.id.Horizontal_ForColNum);
                lockableHorizontalScrollView.setScrollX(x);
                break;

                default:
                    break;
        }
    }

    //グリッド縦スクロール
    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy){
        int id = scrollView.getId();
        switch(id)
        {
            case R.id.ScrollView_ForGrid:
                //縦合わせる
                LockableScrollView lockableScrollView=
                        (LockableScrollView)findViewById(R.id.ScrollView_ForRowNum);
                lockableScrollView.setScrollY(y);
                break;

                default:
                    break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSetRowsAndColumns(InputRowAndColumnFragment fragment,int rows, int columns){
        //todo 行列数が帰ってきたときの処理
    }

    @Override
    public void onGoToNext(InputRowAndColumnFragment fragment){
        //todo つぎへボタンが押されたとき
    }
}
