package com.example.sde2.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;


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
    private int numRows=0;
    private int numCols=0;

    CheckBox[][] checkBoxes;

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
        // チェックボックス用意
        checkBoxes = new CheckBox[numRows][numCols]; //1次元に？
        //チェックしたものをGridViewに追加
        for (int i=0;i<numRows;i++) {
            for (int j=0;j<numCols;j++) {
                checkBoxes[i][j] = new CheckBox(getActivity());
                checkBoxes[i][j].setChecked(true);
            }
        }
    }

    //onCreateの直後
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //GridLayoutにチェックボックスを追加
        View view =
                inflater.inflate(R.layout.fragment_seat_grid, container, false);
        GridLayout gridLayout =
                (GridLayout) view.findViewById(R.id.GridLayout_checkboxesContainer);
        gridLayout.removeAllViews();

        for(int i=0;i<numRows;i++) {
            //行数表示
            GridLayout.LayoutParams params1 = new GridLayout.LayoutParams();
            params1.rowSpec=GridLayout.spec(i);
            params1.columnSpec=GridLayout.spec(0);
            int margin = 30;
            params1.setMargins(margin,margin,margin,margin);

            TextView rowText = new TextView(getActivity());
            rowText.setText(String.valueOf(i+1));
            rowText.setGravity(Gravity.CENTER);
            gridLayout.addView(rowText,params1);

            for (int j=0;j<numCols;j++) {
                //チェックボックスをセット
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec=GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j+1);

                params.setMargins(margin,margin,margin,margin);
                gridLayout.addView(checkBoxes[i][j],params);
            }
        }

        // todo: gridlayoutの整形
//        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
//        params.columnSpec = GridLayout.spec()


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
}
