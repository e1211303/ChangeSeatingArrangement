package com.example.sde2.myapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class AsyncTask_ForCheckboxGrid extends AsyncTask<Void,Void,Void> {


    private RowAndColumnActivity context;
    private ProgressDialog dialog;
    private static final String ARG_NUM_ROWS = "param1";
    private static final String ARG_NUM_COLS = "param2";


    public AsyncTask_ForCheckboxGrid(RowAndColumnActivity context)
    {
        this.context=context;
    }

    //実行前
    @Override
    protected void onPreExecute()
    {
        dialog = new ProgressDialog(context);
        dialog.setTitle(context.getResources().getString(R.string.Title_PreparingGrid));
        dialog.setMessage(context.getResources().getString(R.string.Message_PreparingGrid));
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.show();
    }

    //処理内容
    //todo: UI変更のポストはまとめたほうがいいかな？
    @Override
    protected Void doInBackground(Void... params)
    {
        //UI変更用
        Handler handler = new Handler(Looper.getMainLooper());

        //spinner自体を取得
        Spinner spinnerRow = context.findViewById(R.id.spinner_row);
        Spinner spinnerCol = context.findViewById(R.id.spinner_column);

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
        context.getSupportFragmentManager().beginTransaction()
                .replace(R.id.GridContainer,fragment)
                .commit();



        //行列番号表示部
        int size = (int)(MyUtil_ForDpAndPx.convertDp2Px(
                context.getResources().getInteger(R.integer.GridSize),context)
                + 0.5);
        size += (int)(MyUtil_ForDpAndPx.convertDp2Px(
                context.getResources().getInteger(R.integer.GridMargin),context)
                + 0.5)*2;
        //列番号について
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size,size);
        layoutParams.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
        final LinearLayout linear_col = context.findViewById(R.id.LinearLayout_colNums);
        handler.post(new Runnable() {
            @Override
            public void run() {
                linear_col.removeAllViews();
            }
        });

        for(int i=0;i<cols;i++)
        {
            //追加するテキストview
            final TextView textView = new TextView(context);
            textView.setText(String.valueOf(i+1));
            textView.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    linear_col.addView(textView,layoutParams);
                }
            });
        }

        //行についても
        final LinearLayout.LayoutParams params2= new LinearLayout.LayoutParams(size,size);
        params2.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
        final LinearLayout linear_row = context.findViewById(R.id.LinearLayout_rowNums);

        handler.post(new Runnable() {
            @Override
            public void run() {
                linear_row.removeAllViews();
            }
        });

        for(int i=0;i<rows;i++)
        {
            //追加するテキスト
            final TextView textView = new TextView(context);
            textView.setText(String.valueOf(i+1));
            textView.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    linear_row.addView(textView,params2);
                }
            });
        }

        //次へボタンを有効化
        final Button button_next = context.findViewById(R.id.button_next);
        handler.post(new Runnable() {
            @Override
            public void run() {
                button_next.setEnabled(true);
            }
        });
        return null;
    }

    //終わったらしたいこと
    @Override
    protected void onPostExecute(Void result){
        dialog.dismiss();
    }
}
