package com.example.sde2.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class LotteryActivity extends AppCompatActivity
    implements
        SeatGridFragment.OnFragmentInteractionListener,
        View.OnClickListener
{

    private long mGridID = -1;
    private final String KEY_GRID_ID = "GridID";

    private final String TAG_SEAT_GRID = "SeatGridFragment";

    private final int REQUEST_CODE_MANUAL_SETTING = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);
        //引数取得
        mGridID = getIntent().getLongExtra(KEY_GRID_ID,-1);

        //フラグメントに渡すための引数
        Bundle bundle = new Bundle();
        bundle.putLong(SeatGridFragment.ARG_GridID,mGridID);

        //フラグメントセット
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.FrameLayout_SeatGridContainer,
                        SeatGridFragment.newInstance(mGridID),
                        TAG_SEAT_GRID)
                .commit();

        //下部のボタンにリスナー（this）をセット
        Button button = (Button)findViewById(R.id.Button_ManualSetting);
        button.setOnClickListener(this);

        button = (Button)findViewById(R.id.Button_StartLottery);
        button.setOnClickListener(this);
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


    @Override
    public void CouldNotFetchFromDatabase(SeatGridFragment fragment){
        throw new RuntimeException(getApplicationContext().toString()
            + "Fetching SeatState Error.");
    }

    @Override
    public void onClick(View v){
        final int id = v.getId();
        if(v instanceof Button){
            MyUtil_ForButton.disableButtonForMillisecs((Button) v,1000);
        }
        switch (id)
        {
            case R.id.Button_ManualSetting:
                //todo 手動設定開始
                Intent intent = new Intent(getApplication(),ManualSettingActivity.class);
                intent.putExtra(ManualSettingActivity.ARG_GRID_ID,mGridID);
                startActivityForResult(intent,REQUEST_CODE_MANUAL_SETTING);
                break;

            case R.id.Button_StartLottery:
                //todo くじ引き開始
                //いきなり結果表示してみるか
                startLottery();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch(requestCode){
            //手動設定から帰ってきた
            case REQUEST_CODE_MANUAL_SETTING:
                if(resultCode != RESULT_OK)
                    return;

                //SeatGrid更新
                SeatGridFragment fragment =  (SeatGridFragment)getSupportFragmentManager()
                        .findFragmentByTag(TAG_SEAT_GRID);
                fragment.prepareSeatGrid(mGridID,null);
                break;
        }
    }

    private boolean startLottery(){
        Bundle[][] SeatStates = getSeatStateFromDB();
        if(SeatStates==null)
            return false;

        final int rows = SeatStates.length;
        final int cols = SeatStates[0].length;


        final String Col_isEnabled = HelperForSeatGridDB.SeatStateConstants.ColName_isEnabled;
        final String Col_isScoped = HelperForSeatGridDB.SeatStateConstants.ColName_isScoped;
        final String Col_isEmpty = HelperForSeatGridDB.SeatStateConstants.ColName_isEmpty;
        final String Col_StudentID = HelperForSeatGridDB.SeatStateConstants.ColName_StudentID;


        //空席でない数をしらべる。入れる位置を確認　すでに入っている番号を記憶
        int count = 0;
        ArrayList<Integer> usedNums = new ArrayList<>();
        ArrayList<Bundle> EnablePoses = new ArrayList<Bundle>();
        final String KEY_ROW = "param1";
        final String KEY_COL = "param2";

        for(int i=0;i<rows;++i){
            for(int j=0;j<cols;++j){
                Bundle state = SeatStates[i][j];

                //空席設定は関係なし
                final boolean isEmpty = state.getBoolean(Col_isEmpty);
                if(isEmpty)
                    continue;

                ++count;

                //決まってなかったら位置を記憶
                //決まっていたらその数字を記憶
                final boolean isEnable = state.getBoolean(Col_isEnabled);
                if(isEnable){
                    Bundle b = new Bundle();
                    b.putInt(KEY_ROW,i);
                    b.putInt(KEY_COL,j);
                    EnablePoses.add(b);

                }else{
                    final String studentID = state.getString(Col_StudentID);
                    int id = Integer.parseInt(studentID);
                    usedNums.add(id);

                    if(id == 0)
                        return false;   //ないと思う
                }
            }
        }

        //count - usedNumsの数 = このくじで入る人数
        for(int i=1;i <= count; ++i){
            //すでに使われているなら飛ばす
            boolean found = false;
            for(int used_id : usedNums){
                if(i==used_id){
                    found = true;
                    break;
                }
            }
            if(found)
                continue;

            //入れる席を決める
            int index = new Random().nextInt(EnablePoses.size());
            Bundle bundle = EnablePoses.get(index);
            final int row = bundle.getInt(KEY_ROW);
            final int col = bundle.getInt(KEY_COL);

            //変更
            Bundle state = SeatStates[row][col];
            state.putBoolean(Col_isEmpty,false);
            state.putBoolean(Col_isEnabled,false);
            state.putString(Col_StudentID,String.valueOf(i));

            //入れる席から外す
            EnablePoses.remove(index);
        }

        //Stateを反映させる

        //大きさなどを取得しておく
        final int GridWidth =
                (int)(MyUtil_ForDpAndPx.convertDp2Px(
                        getResources().getInteger(R.integer.GridWidth_ForTextView),
                        this
                )+0.5);

        final int GridHeight =
                (int)(MyUtil_ForDpAndPx.convertDp2Px(
                        getResources().getInteger(R.integer.GridHeight_ForTextView),
                        this
                )+0.5);

        final int GridMargin =
                (int)(MyUtil_ForDpAndPx.convertDp2Px(
                        getResources().getInteger(R.integer.GridMargin),
                        this
                )+0.5);

        SeatGridFragment fragment =
                (SeatGridFragment) getSupportFragmentManager()
                .findFragmentByTag(TAG_SEAT_GRID);

        View view = fragment.getView();
        GridLayout gridLayout = view.findViewById(R.id.GridLayout_Container);
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(cols);

        TextView textView;
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){


                //席情報のバンドル取得
                final Bundle seatState = SeatStates[i][j];

                //席状態取得
                final boolean is_empty = seatState.getBoolean(Col_isEmpty);
                final boolean is_enabled = seatState.getBoolean(Col_isEnabled);
                final boolean is_scoped = seatState.getBoolean(Col_isScoped);
                final String studentID = seatState.getString(Col_StudentID);

                //席情報に応じたこのtextViewの設定
                textView = new TextView(this);
                //共通設定はここに
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
                textView.setBackgroundResource(R.drawable.my_border_for_textview);

                //空席設定か？
                if(is_empty == true){
                    //空席の場合 灰色の斜体
                    textView.setText(getResources().getString(R.string.EmptySeat));
                    textView.setTypeface(null,Typeface.ITALIC);
                    textView.setTextColor(getResources().getColor(R.color.Gray_ForText));
                    textView.setBackgroundColor(
                            getResources().getColor(R.color.BackGround_ForEmptySeat));
                }
                //以降空席設定でない場合
                else{
                    //まだ決まってないかどうか
                    if(is_enabled == true) {
                        //まだ入れる場合
                        textView.setText("");
                    }else{
                        //決まっている場合
                        textView.setText(studentID);    //todo IDから名前を入れる
                    }

                    //スコープ設定か
                    if(is_scoped == true){
                        //スコープ設定の場合 背景を変える
                        textView.setBackgroundColor(
                                getResources().getColor(R.color.Background_ForScopedSeat));
                    }
                }

                //できたtextviewをセット
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = GridWidth;
                params.height = GridHeight;
                params.setMargins(GridMargin,GridMargin,GridMargin,GridMargin);
                gridLayout.addView(textView,params);
            }
        }

        return true;
    }

    //mIDから席情報をとってくる
    private Bundle[][] getSeatStateFromDB(){
        HelperForSeatGridDB helper = new HelperForSeatGridDB(getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        //SeatGridについて
        //列名 長かったので新たな変数に
        final String TableName = HelperForSeatGridDB.SeatGridConstants.TableName;
        final String Col_ID = HelperForSeatGridDB.SeatGridConstants.ColName_ID;
        final String Col_Name = HelperForSeatGridDB.SeatGridConstants.ColName_Name;
        final String Col_Rows = HelperForSeatGridDB.SeatGridConstants.ColName_Rows;
        final String Col_Cols = HelperForSeatGridDB.SeatGridConstants.ColName_Cols;

        //IDに対応したSeatGrid取得 cursorは先頭-1の位置で帰ってくるらしい
        Cursor cursor = db.query(TableName,
                null,
                Col_ID + "=?",
                new String[]{String.valueOf(mGridID)},
                null,null,null);

        Bundle SeatGridBundle = new Bundle();
        try{
            if(cursor.moveToNext()==true){
                //表の名前
                String table_name = cursor.getString(cursor.getColumnIndex(Col_Name));
                SeatGridBundle.putString(Col_Name,table_name);
                //行数
                int rows = cursor.getInt(cursor.getColumnIndex(Col_Rows));
                SeatGridBundle.putInt(Col_Rows,rows);
                //列数
                int cols = cursor.getInt(cursor.getColumnIndex(Col_Cols));
                SeatGridBundle.putInt(Col_Cols,cols);

            }
        }catch (Exception e){
            SeatGridBundle = null;
        }finally{
            cursor.close();
        }

        if(SeatGridBundle == null){
            //表を取得できなかった
            return null;
        }

        final int rows = SeatGridBundle.getInt(Col_Rows);
        final int cols = SeatGridBundle.getInt(Col_Cols);

        //SeatStateについて
        //列名　長かったので（略
        final String TableName1 = HelperForSeatGridDB.SeatStateConstants.TableName;
        final String Col_ID1 = HelperForSeatGridDB.SeatStateConstants.ColName_ID;
        final String Col_Row = HelperForSeatGridDB.SeatStateConstants.ColName_Row;
        final String Col_Col = HelperForSeatGridDB.SeatStateConstants.ColName_Col;
        final String Col_isEnabled = HelperForSeatGridDB.SeatStateConstants.ColName_isEnabled;
        final String Col_isScoped = HelperForSeatGridDB.SeatStateConstants.ColName_isScoped;
        final String Col_isEmpty = HelperForSeatGridDB.SeatStateConstants.ColName_isEmpty;
        final String Col_StudentID = HelperForSeatGridDB.SeatStateConstants.ColName_StudentID;
        //バンドルの配列に格納
        Bundle[][] SeatStateBundles = new Bundle[rows][cols];
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                //席を検索
                cursor=db.query(TableName1,
                        null,
                        Col_ID1 + "=? AND " + Col_Row + "=? AND " + Col_Col+"=?",
                        new String[] {String.valueOf(mGridID), String.valueOf(i), String.valueOf(j)},
                        null,null,null);
                SeatStateBundles[i][j] = new Bundle();
                try{
                    if(cursor.moveToNext()==true){
                        //各項目取得　うえを参考に
                        //空席設定か
                        int is_empty_int = cursor.getInt(cursor.getColumnIndex(Col_isEmpty));
                        //まだ入れるか
                        int is_enabled_int = cursor.getInt(cursor.getColumnIndex(Col_isEnabled));
                        //スコープ設定か
                        int is_scoped_int = cursor.getInt(cursor.getColumnIndex(Col_isScoped));
                        //学生IDは今のところ省略
                        String student_id = cursor.getString(cursor.getColumnIndex(Col_StudentID));

                        //整数値をbooleanに
                        boolean is_empty = (is_empty_int != 0) ? true : false;
                        boolean is_enabled = (is_enabled_int !=0) ? true : false;
                        boolean is_scoped = (is_scoped_int != 0) ? true : false;

                        //Bundleに格納
                        Bundle tmp = SeatStateBundles[i][j];
                        tmp.putBoolean(Col_isEmpty,is_empty);
                        tmp.putBoolean(Col_isEnabled,is_enabled);
                        tmp.putBoolean(Col_isScoped,is_scoped);
                        tmp.putString(Col_StudentID,student_id);
                    }
                }catch (Exception e){
                    SeatStateBundles[i][j] = null;
                    SeatGridBundle = null;
                }finally {
                    cursor.close();
                }

                if(SeatStateBundles[i][j] == null)
                    return null;
            }
        }


        return SeatStateBundles;
    }

}
