package com.example.sde2.myapplication;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.constraint.solver.widgets.Helper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


public class RowAndColumnActivity extends AppCompatActivity
        implements
        SeatGridFragment.OnFragmentInteractionListener,
        InputRowAndColumnFragment.OnFragmentInteractionListener,
        InputUsingScopeSettingFragment.OnFragmentInteractionListener
{

    private static final String ARG_NUM_ROWS = "param1";
    private static final String ARG_NUM_COLS = "param2";

    private static final String TAG_SEAT_GRID = "SeatGridFragment";
    private static final String TAG_SCOPE ="ScopeFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_row_and_column);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.RelativeLayout_ForInputPrompt,new InputRowAndColumnFragment())
                .commit();
    }


    @Override
    public void onSetRowsAndColumns(final InputRowAndColumnFragment inputRowAndColumnFragment, final int rows, final int columns)
    {
        //引数をまとめたもの
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_NUM_ROWS,rows);
        bundle.putInt(ARG_NUM_COLS,columns);

        //フラグメントの実体を生成
        SeatGridFragment seatGridFragment = new SeatGridFragment();
        seatGridFragment.setArguments(bundle);

        //フラグメント差し込み
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.RelativeLayout_For_Display_Bottom,seatGridFragment,TAG_SEAT_GRID)
                .commit();
    }

    //todo 前のフラグメントの状態保持？
    //行列入力部の次へで
    @Override
    public void onGoToNext(InputRowAndColumnFragment fragment)
    {
        //下部のグリッドを取得できるか
        Fragment fragmentBottom=
                getSupportFragmentManager()
                        .findFragmentByTag(TAG_SEAT_GRID);
        if(!(fragmentBottom instanceof SeatGridFragment))
            return; //何もせず

        SeatGridFragment seatGridFragment = (SeatGridFragment)fragmentBottom;

        //入力部fragment差し替え
        InputUsingScopeSettingFragment inputUsingScopeSettingFragment=
                new InputUsingScopeSettingFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.RelativeLayout_ForInputPrompt,inputUsingScopeSettingFragment,TAG_SCOPE)
                .commit();

        //空席状態を取得
        Boolean[][] seatState =
                seatGridFragment.getIsCheckedAll();
        //falseのところをnullに
        final int rows = seatState.length;
        final int cols = seatState[0].length;
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                Boolean b = seatState[i][j];
                if(b != null && b.booleanValue() == false){
                    seatState[i][j] = null;
                }
            }
        }

        //空席部分以外のGridを表示
        seatGridFragment.prepareSeatGrid(rows,cols,seatState,null);
        //チェックを外す
        seatGridFragment.setIsCheckedAll(false);


    }

    //DB操作用
    private HelperForSeatGridDB mHelperForSeatGrid=null;
    private SQLiteDatabase db=null;

    //スコープ設定の次へで
    @Override
    public void onGoToNext(InputUsingScopeSettingFragment fragment){
        //下部のグリッドを取得できるか
        Fragment fragmentBottom=
                getSupportFragmentManager()
                        .findFragmentByTag(TAG_SEAT_GRID);
        if(!(fragmentBottom instanceof SeatGridFragment))
            return; //何もせず

        SeatGridFragment seatGridFragment = (SeatGridFragment)fragmentBottom;

        //席状態取得
        //null->空席　true->スコープ内　false->スコープ外
        final Boolean[][] seatStates =
                seatGridFragment.getIsCheckedAll();

        final int rows = seatStates.length;
        final int cols = seatStates[0].length;

        //Gridの名前を聞いてDBに保存
        LayoutInflater inflater =
                LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_edit_text,null);
        final EditText editText = (EditText)view.findViewById(R.id.EditText_ForDialog);

        //DB準備
        setDataBase();

        //OK押したらDBに追加する
        final Context context = this;
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(getResources().getString(R.string.Ask_for_GridName))
                .setView(view);

        alertDialog.setPositiveButton(getResources().getString(R.string.OK),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String GridName = editText.getText().toString();
                        saveSeatState(seatStates,GridName);
                        //席情報を保存
                        long GridID = saveSeatState(seatStates,GridName);
                        if(GridID != -1){
                            // todo このactivity終了（席情報を渡して）
                            Log.i("DB登録","追加成功");
                            Intent data = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString("GridID",String.valueOf(GridID));
                            data.putExtras(bundle);

                            setResult(Activity.RESULT_OK,data);
                            finish();
                        }else{
                            //todo alartdialogでメッセージ　何もしない
                            new AlertDialog.Builder(context)
                                    .setTitle(getResources().getString(R.string.Title_SeatGrid_CouldNotBeAdded))
                                    .show();
                        }
                    }
                })
                .show();

    }




    private void setDataBase()
    {
        mHelperForSeatGrid = new HelperForSeatGridDB(getApplicationContext());
        db = mHelperForSeatGrid.getWritableDatabase();
    }

    //todo なんか2回呼ばれる
    //座席状態で保存。いつかは人名入り
    private long saveSeatState(final Boolean[][] seatStates,final String GridName){
        if(db==null) return -1;
        if(seatStates==null) return -1;

        int rows = seatStates.length;
        int cols = seatStates[0].length;

        //SeatGrid分
        ContentValues values_SeatGrid = new ContentValues();
        values_SeatGrid.put(HelperForSeatGridDB.SeatGridConstants.ColName_Name,GridName);
        values_SeatGrid.put(HelperForSeatGridDB.SeatGridConstants.ColName_Rows,rows);
        values_SeatGrid.put(HelperForSeatGridDB.SeatGridConstants.ColName_Cols,cols);

        //トランザクション開始
        db.beginTransaction();
        final long GridID = //insertされた列の_idが返ってくる？
                db.insert(HelperForSeatGridDB.SeatGridConstants.TableName,
                        null,
                        values_SeatGrid);
        if(GridID == -1){
            db.endTransaction();
            return -1;
        }

        //SeatState分
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                //座席状態一つ追加
                ContentValues values_SeatState = new ContentValues();
                values_SeatState.put(HelperForSeatGridDB.SeatStateConstants.ColName_ID,GridID);
                values_SeatState.put(HelperForSeatGridDB.SeatStateConstants.ColName_Row,i);
                values_SeatState.put(HelperForSeatGridDB.SeatStateConstants.ColName_Col,j);

                //席状態を確認
                Boolean state = seatStates[i][j];
                //これは空席設定の場合
                boolean isEmpty = true; //空席設定か
                boolean isEnabled = false;  //空席設定でなく、まだ誰もいないならtrue
                boolean isScoped = false;   //スコープ内か
                if(state != null){  //空席ではなかった
                    isEmpty=false;
                    isEnabled = true;   //まだ入ってないので
                    isScoped = state.booleanValue();
                }

                //3列分
                values_SeatState.put(HelperForSeatGridDB.SeatStateConstants.ColName_isEmpty,isEmpty);
                values_SeatState.put(HelperForSeatGridDB.SeatStateConstants.ColName_isEnabled,isEnabled);
                values_SeatState.put(HelperForSeatGridDB.SeatStateConstants.ColName_isScoped,isScoped);

                //まだ入っていないので
                values_SeatState.put(HelperForSeatGridDB.SeatStateConstants.ColName_StudentID,"");

                //挿入
                long ret = db.insert(HelperForSeatGridDB.SeatStateConstants.TableName,null,values_SeatState);
                if(ret == -1){
                    db.endTransaction();
                    return -1;
                }
            }
        }
        //コミットされる
        db.setTransactionSuccessful();
        db.endTransaction();
        return GridID;
    }
}
