package com.example.sde2.myapplication;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

//Gridに入れるものを変えただけ。　todo クラス一覧から選ぶとかそんな感じにいつかしたい
public class TextBoxGridFragment extends SeatGridFragment
implements
        AdapterView.OnItemSelectedListener
{

    public static TextBoxGridFragment newInstance(long GridID){
        TextBoxGridFragment fragment = new TextBoxGridFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GridID,GridID);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_seat_grid, container, false);
//
//        //DBの情報をもとにGrid用意
//        if(prepareSeatGrid(mGridID,view)==false){
//            mListener.CouldNotFetchFromDatabase(this);
//        }
//
//        return view;
//    }

    //データベースからの席設定を基にスピンボックス生成
    @Override
    public boolean prepareSeatGrid(long GridID, View view) {
        if(view == null)
            view = getView();

        if(mHelper == null)
            mHelper = new HelperForSeatGridDB(getActivity().getApplicationContext());
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //データベースから情報と取り出す

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
                new String[]{String.valueOf(GridID)},
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
            return false;
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
                        new String[] {String.valueOf(GridID), String.valueOf(i), String.valueOf(j)},
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
                    return false;
            }
        }

        //取り出した情報をもとにGridをセット
        if(view == null)
            view = getView();

        //格納先のGridLayout取得
        GridLayout gridLayout =
                (GridLayout)view.findViewById(R.id.GridLayout_Container);
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(cols);

        //大きさなどを取得しておく
        final int GridWidth =
                (int)(MyUtil_ForDpAndPx.convertDp2Px(
                        getResources().getInteger(R.integer.GridWidth_ForSpinner),
                        getActivity()
                )+0.5);

        final int GridHeight =
                (int)(MyUtil_ForDpAndPx.convertDp2Px(
                        getResources().getInteger(R.integer.GridHeight_ForSpinner),
                        getActivity()
                )+0.5);

        final int GridMargin =
                (int)(MyUtil_ForDpAndPx.convertDp2Px(
                        getResources().getInteger(R.integer.GridMargin),
                        getActivity()
                )+0.5);

        //---------------
        //空席設定でない席数
        int notEmptyCount =0;
        for(int i=0;i<rows;++i){
            for(int j=0;j<cols;++j){
                boolean isEmpty = SeatStateBundles[i][j].getBoolean(Col_isEmpty);
                if(!isEmpty)
                    ++notEmptyCount;
            }
        }
        final int numOfStudent = notEmptyCount; //確定した有効席数

        //todo 席状態に応じて適切なSpinnerを追加

        for(int i=0; i<rows; ++i){
            for(int j=0; j<cols; ++j){
                //席情報取得
                final Bundle seatState = SeatStateBundles[i][j];

                //席状態取得
                final boolean is_empty = seatState.getBoolean(Col_isEmpty);
                final boolean is_enabled = seatState.getBoolean(Col_isEnabled);
                final boolean is_scoped = seatState.getBoolean(Col_isScoped);
                final String studentID = seatState.getString(Col_StudentID);

                //空席設定か 空席用テキストビュー
                if(is_empty){
                    TextView textView = new TextView(getActivity());
                    textView.setGravity(Gravity.CENTER);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
                    textView.setBackgroundResource(R.drawable.my_border_for_textview);
                    textView.setText(getResources().getString(R.string.EmptySeat));
                    textView.setTypeface(null,Typeface.ITALIC);
                    textView.setTextColor(getResources().getColor(R.color.Gray_ForText));
                    textView.setBackgroundColor(
                            getResources().getColor(R.color.BackGround_ForEmptySeat));

                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = GridWidth;
                    params.height = GridHeight;
                    params.setMargins(GridMargin,GridMargin,GridMargin,GridMargin);
                    gridLayout.addView(textView,params);
                    continue;
                }
                else
                {
                    //スピナーを入れる
                    Spinner spinner = new Spinner(getActivity());
                    setSpinner(spinner,numOfStudent);   //項目をセット

                    //すでに入っている場合
                    if(is_enabled == false) {
                        //学生番号を取得
                        int id;
                        if(studentID!=null)
                            id = Integer.parseInt(studentID);
                        else
                            id =0;  //未選択
                        spinner.setSelection(id);
                    }

                    //Gridにセット
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = GridWidth;
                    params.height = GridHeight;
                    params.setMargins(GridMargin,GridMargin,GridMargin,GridMargin);
                    gridLayout.addView(spinner,params);

                    //スコープ設定か
//                    if(is_scoped == true){
//                        //スコープ設定の場合 背景を変える
//                        spinner.setBackgroundColor(
//                                getResources().getColor(R.color.Background_ForScopedSeat));
//                    }
                }
            }
        }


        //---------------


        //todo ここらへんをまとめたviewにしたいな

        //行列表示部の設定
        //行数表示部の設定
        LockableScrollView VerticalScroll =
                view.findViewById(R.id.ScrollView_ForRowNum);
        VerticalScroll.setScrollingEnabled(false);
        VerticalScroll.setVerticalScrollBarEnabled(false);
        //LinearLayoutに行数表示
        int size = GridHeight+GridMargin*2;
        LinearLayout linearLayout_row = view.findViewById(R.id.LinearLayout_ForRowNums);
        linearLayout_row.removeAllViews();
        for(int i=0;i<rows;i++)
        {
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,size);
            layoutParams.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
            TextView textView1 = new TextView(getActivity());
            textView1.setText(String.valueOf(i+1));
            textView1.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
            linearLayout_row.addView(textView1,layoutParams);
        }

        //列数表示部の設定
        size = GridWidth+GridMargin*2;
        LockableHorizontalScrollView HorizontalScroll =
                view.findViewById(R.id.Horizontal_ForColNum);
        HorizontalScroll.setScrollingEnabled(false);
        HorizontalScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout linearLayout_col =
                view.findViewById(R.id.LinearLayout_ForColNums);
        linearLayout_col.removeAllViews();
        for(int i=0;i<cols;i++){
            LinearLayout.LayoutParams layoutParams=
                    new LinearLayout.LayoutParams(size,LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            TextView textView1 = new TextView(getActivity());
            textView1.setText(String.valueOf(i+1));
            textView1.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            linearLayout_col.addView(textView1,layoutParams);
        }

        //スクロール通知を受け取る設定
        ObservableScrollView observableScrollView=
                view.findViewById(R.id.ScrollView_ForGrid);
        observableScrollView.setOnScrollViewListener(this);

        ObservableHorizontalScrollView observableHorizontalScrollView=
                view.findViewById(R.id.HorizontalScrollView_ForGrid);
        observableHorizontalScrollView.setOnScrollViewListener(this);

        return true;
    }

    //有効席数からスピナーをセット　0からnum
    private void setSpinner(Spinner spinner, int numOfNonEmpties){
        String[] stringArray = new String[numOfNonEmpties + 1];

        stringArray[0] = "(未選択)";
        for(int i=1; i<=numOfNonEmpties; ++i){
            stringArray[i] = String.valueOf(i);
        }

        ArrayAdapter adapter =
                new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,stringArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    //スピナーで選択されたとき
    @Override
    public void onItemSelected(AdapterView parent, View view, int position, long id) {
        checkSpinners();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }

    //スピナーの重複を調べる関数。色も付ける
    public boolean checkSpinners(){
        ArrayList<Spinner> spinners = getSpinners();

        final int num = spinners.size();


        //すべてのスピナーについて、選択された数字を取得
        final String KEY_INDEX = "param1";
        final String KEY_SELECTED_NUM = "param2";
        ArrayList<Bundle> selectedItemAndIndexBundle = new ArrayList<Bundle>();

        for(int indexInSpinners=0;indexInSpinners<num;++indexInSpinners){
            Spinner spinner = spinners.get(indexInSpinners);
            final int selectedNum = spinner.getSelectedItemPosition();
            if(selectedNum != 0){   //未選択について記憶
                //スピナー添字と内容をまとめておく
                Bundle bundle = new Bundle();
                bundle.putInt(KEY_INDEX,indexInSpinners);
                bundle.putInt(KEY_SELECTED_NUM,selectedNum);

                //配列に格納
                selectedItemAndIndexBundle.add(bundle);
            }else{
                //未選択のものは無条件に普通の表示
                spinner.setBackgroundColor(
                        getResources().getColor(R.color.Background_ForNonDuplicatedSpinner));
            }

        }

        //取得された数字について重複を調べる
        boolean hasDuplication = false; //全体を通して重複があったか
        while(selectedItemAndIndexBundle.isEmpty()==false){
            //先頭のBundleを取得
            Bundle nowBundle = selectedItemAndIndexBundle.get(0); //注目点(先頭)の情報

            boolean flag_removed = false;   //今の先頭と比較して重複があったか
            //末尾から順番に、先頭要素と比較
            for(int at = selectedItemAndIndexBundle.size() - 1; 1 <= at; --at){
                Bundle watchBundle = selectedItemAndIndexBundle.get(at);
                int nowSelected = nowBundle.getInt(KEY_SELECTED_NUM);
                int watchSelected = watchBundle.getInt(KEY_SELECTED_NUM);

                //判定に応じてこれの色を変えたりする
                final int spinnerIndex = watchBundle.getInt(KEY_INDEX); //格納先
                Spinner watchSpinner = spinners.get(spinnerIndex);  //注目のスピナー

                //重複を判定
                if(nowSelected == watchSelected){
                    //重複していた
                    hasDuplication = true;
                    //重複したスピナーを強調
                    watchSpinner.setBackgroundColor(
                            getResources().getColor(R.color.Background_ForDuplicatedSpinner));
                    //検索対象から削除
                    selectedItemAndIndexBundle.remove(at);
                    flag_removed = true;
                }
                else{
                    //重複していない　色を通常に　削除はしない
                    watchSpinner.setBackgroundColor(
                            getResources().getColor(R.color.Background_ForNonDuplicatedSpinner));
                }
            }

            //基準のスピナーを取得
            final int index = nowBundle.getInt(KEY_INDEX);
            Spinner spinner = spinners.get(index);

            //基準のスピナーの後処理
            if(flag_removed==true){
                //１つでも重複があったとき同様に重複の色に
                spinner.setBackgroundColor(
                    getResources().getColor(R.color.Background_ForDuplicatedSpinner));
            }else{
                //重複がなければ色を戻す
                spinner.setBackgroundColor(
                        getResources().getColor(R.color.Background_ForNonDuplicatedSpinner));
            }

            //基準だった先頭要素を検索対象から削除する
            selectedItemAndIndexBundle.remove(0);
        }

        return (!hasDuplication);
    }

    //現在の状態でDB更新
    public boolean updateDB()
    {
        if(mHelper == null)
            mHelper = new HelperForSeatGridDB(getActivity().getApplicationContext());
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //行列数
        View view = getView();
        GridLayout gridLayout = view.findViewById(R.id.GridLayout_Container);
        if(gridLayout == null)
            return false;

        //長方形の前提
        final int rows = gridLayout.getRowCount();
        final int cols = gridLayout.getColumnCount();

        //各席の状態を更新
        for(int i=0;i<rows;++i){
            for(int j=0;j<cols;++j){
                //その位置のview取得
                View gotView = gridLayout.getChildAt(i*cols + j);

                //多分空席
                if((gotView instanceof Spinner)==false)
                    continue;

                //スピナーから選択状態を確認
                Spinner spinner = (Spinner) gotView;
                final int selected = spinner.getSelectedItemPosition();

                //データベース用
                final String TableName1 = HelperForSeatGridDB.SeatStateConstants.TableName;
                final String Col_ID1 = HelperForSeatGridDB.SeatStateConstants.ColName_ID;
                final String Col_Row = HelperForSeatGridDB.SeatStateConstants.ColName_Row;
                final String Col_Col = HelperForSeatGridDB.SeatStateConstants.ColName_Col;
                final String Col_isEnabled = HelperForSeatGridDB.SeatStateConstants.ColName_isEnabled;
                final String Col_StudentID = HelperForSeatGridDB.SeatStateConstants.ColName_StudentID;

                ContentValues cv = new ContentValues();
                final String Where =
                        Col_ID1 + " = ? AND " +
                        Col_Row + " = ? AND " +
                        Col_Col + " = ?";
                final String[] WhereArgs =
                        new String[] {String.valueOf(mGridID),String.valueOf(i),String.valueOf(j)};
                //未選択と選択済みに応じた更新内容
                if(selected == 0){
                    //未選択設定 enabled studentid=0
                    //設定値
                    cv.put(Col_isEnabled,true);
                    cv.put(Col_StudentID,(String)null);
                }else{
                    //選択済み
                    cv.put(Col_isEnabled,false);
                    cv.put(Col_StudentID,String.valueOf(selected));
                }

                //update実行
                db.update(TableName1,cv,Where,WhereArgs);
            }
        }

        return true;
    }

    private ArrayList<Spinner> getSpinners(){
        View view = getView();
        GridLayout gridLayout = (GridLayout)view.findViewById(R.id.GridLayout_Container);

        //すべてのスピナーを取得
        final int numOfViews = gridLayout.getChildCount();
        ArrayList<Spinner> spinners = new ArrayList<Spinner>();

        for(int i=0;i<numOfViews;++i){
            View gotView = gridLayout.getChildAt(i);
            if(gotView instanceof Spinner){
                spinners.add((Spinner)gotView);
            }
        }
        return spinners;
    }
}
