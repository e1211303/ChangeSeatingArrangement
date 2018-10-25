package com.example.sde2.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;


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
    private static final String ARG_NUM_ROWS = "param1";
    private static final String ARG_NUM_COLS = "param2";

    // TODO: Rename and change types of parameters
    // 行数と列数の記憶
    private int numRows;
    private int numCols;

    private OnFragmentInteractionListener mListener;

    public SeatGridFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param numRows Parameter 1.
     * @param numCols Parameter 2.
     * @return A new instance of fragment SeatGridFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SeatGridFragment newInstance(int numRows, int numCols) {
        SeatGridFragment fragment = new SeatGridFragment();
        // 初めて作られた時の引数を記憶する
        Bundle args = new Bundle();
        args.putInt(ARG_NUM_ROWS, numRows);
        args.putInt(ARG_NUM_COLS, numCols);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // 再生成されたときに、すでに受け取っていた引数をメンバ変数にセット
            numRows = getArguments().getInt(ARG_NUM_ROWS);
            numCols = getArguments().getInt(ARG_NUM_COLS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seat_grid, container, false);
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
}
