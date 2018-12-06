package com.example.sde2.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.solver.ArrayLinkedVariables;
import android.support.constraint.solver.widgets.Helper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;


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

    // TODO: Rename and change types of parameters
    private long mGridID = -1;

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
            //状態を復元
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

    private boolean prepareSeatGrid(long GridID,View view){
        if(mHelper == null)
            mHelper = new HelperForSeatGridDB(getActivity().getApplicationContext());
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //todo データベースから情報と取り出す

        //SeatGridについて
        //列名
        String TableName = HelperForSeatGridDB.SeatGridConstants.TableName;
        String Col_ID = HelperForSeatGridDB.SeatGridConstants.ColName_ID;
        String Col_Name = HelperForSeatGridDB.SeatGridConstants.ColName_Name;
        String Col_Rows = HelperForSeatGridDB.SeatGridConstants.ColName_Rows;
        String Col_Cols = HelperForSeatGridDB.SeatGridConstants.ColName_Cols;

        //IDに対応したSeatGrid取得
        Cursor cursor = db.query(TableName,
                null,
                Col_ID + "=?",
                new String[]{String.valueOf(GridID)},
                null,null,null);

        Bundle SeatGridItems = new Bundle();
        try{
            if(cursor.moveToNext()==true){
                //表の名前
                String str = cursor.getString(cursor.getColumnIndex(Col_Name));
                SeatGridItems.putString(Col_Name,str);
                //行数
                str = cursor.getString(cursor.getColumnIndex(Col_Rows));
                SeatGridItems.putString(Col_Rows,str);
                //列数
                str = cursor.getString(cursor.getColumnIndex(Col_Cols));
                SeatGridItems.putString(Col_Cols,str);

            }
        }catch (Exception e){
            SeatGridItems = null;
        }finally{
            cursor.close();
        }

        if(SeatGridItems == null){
            //表を取得できなかった
            return false;
        }

        int rows = Integer.parseInt(SeatGridItems.getString(Col_Rows));
        int cols = Integer.parseInt(SeatGridItems.getString(Col_Cols));


        //SeatStateについて
        TableName = HelperForSeatGridDB.SeatStateConstants.TableName;
        Col_ID = HelperForSeatGridDB.SeatStateConstants.ColName_ID;
        String Col_Row = HelperForSeatGridDB.SeatStateConstants.ColName_Row;
        String Col_Col = HelperForSeatGridDB.SeatStateConstants.ColName_Col;
        String Col_isEnabled = HelperForSeatGridDB.SeatStateConstants.ColName_isEnabled;
        String Col_isScoped = HelperForSeatGridDB.SeatStateConstants.ColName_isScoped;
        String Col_isEmpty = HelperForSeatGridDB.SeatStateConstants.ColName_isEmpty;
        String Col_StudentID = HelperForSeatGridDB.SeatStateConstants.ColName_StudentID;
        //todo バンドルの配列としてしまおう
        Bundle[][] SeatStateBundles = new Bundle[rows][cols];
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                //席を検索
                cursor=db.query(TableName,
                        null,
                        Col_ID+"=?,"+Col_Row+"=?,"+Col_Col+"=?",
                        new String[] {String.valueOf(GridID), String.valueOf(i), String.valueOf(j)},
                        null,null,null);
                SeatStateBundles[i][j] = new Bundle();
                try{
                    if(cursor.moveToNext()==true){
                        //todo 各項目取得　うえを参考に
                    }
                }
            }
        }


    }
}
