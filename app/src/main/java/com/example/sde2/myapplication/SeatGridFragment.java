package com.example.sde2.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SeatGridFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SeatGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SeatGridFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_GridID = "param1";

    //instanceState用
    private static final String InstStateKey_GridID = "param1";
    private static final String InstStateKey_SeatGridBundle = "param2";
    private static final String InstStateKey_SeatStateBundles = "param3";

    // TODO: Rename and change types of parameters
    private long mGridID = -1;
    private Bundle mSeatGridBundle = null;
    private Bundle[][] mSeatStateBundles = null;

    private OnFragmentInteractionListener mListener;

    public SeatGridFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @param GridID
     * @return A new instance of fragment SeatGridFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SeatGridFragment newInstance(long GridID) {
        SeatGridFragment fragment = new SeatGridFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GridID,GridID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //セットされた引数を取り出す
            mGridID = getArguments().getLong(ARG_GridID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seat_grid, container, false);

        //todo DBの情報をもとにGrid用意


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private HelperForSeatGridDB mHelper=null;

    //DBからの情報をもとにしてGridをセット
    private boolean prepareSeatGrid(long GridID,View view){
        if(mHelper == null)
            mHelper = new HelperForSeatGridDB(getActivity().getApplicationContext());
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //todo データベースから情報と取り出す

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
                        Col_ID1+"=?,"+Col_Row+"=?,"+Col_Col+"=?",
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
                        //String student_id = cursor.getString(cursor.getColumnIndex(Col_StudentID));

                        //整数値をbooleanに
                        boolean is_empty = (is_empty_int != 0) ? true : false;
                        boolean is_enabled = (is_enabled_int !=0) ? true : false;
                        boolean is_scoped = (is_scoped_int != 0) ? true : false;

                        //Bundleに格納
                        Bundle tmp = SeatStateBundles[i][j];
                        tmp.putBoolean(Col_isEmpty,is_empty);
                        tmp.putBoolean(Col_isEnabled,is_enabled);
                        tmp.putBoolean(Col_isScoped,is_scoped);
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

        //todo 取り出した情報をもとにGridをセット
        if(view == null)
            view = getView();

        //格納先のGridLayout取得
        GridLayout gridLayout =
                (GridLayout)view.findViewById(R.id.GridLayout_Container);
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(cols);

        //席状態に応じて適切なTextViewをGridに追加
        TextView[][] textViews = new TextView[rows][cols];
        TextView textView;

        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                //席情報に応じたこのtextViewの設定
                textView = new TextView(getActivity());

                //席情報のバンドル取得
                final Bundle seatState = SeatStateBundles[i][j];

                //空席設定か？
                final boolean is_empty = seatState.getBoolean(Col_isEmpty);
                if(is_empty == true){
                    textView.setText(getResources().getString(R.string.EmptySeat));
                    textView.setTextColor(getResources().getColor(R.color.Gray_ForText));
                    textView.setEnabled(false); //無効化いる？
                }

                //todo まだ入れるか

            }
        }


        return false;
    }
}
