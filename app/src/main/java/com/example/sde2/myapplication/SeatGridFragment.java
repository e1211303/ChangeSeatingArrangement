package com.example.sde2.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SeatGridFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SeatGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SeatGridFragment extends Fragment
implements ObservableScrollView.ScrollViewListener,
        ObservableHorizontalScrollView.ScrollViewListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NUM_ROWS = "param1";
    private static final String ARG_NUM_COLS = "param2";

    // TODO: Rename and change types of parameters
    // 行数と列数の記憶
    private int numRows=0;
    private int numCols=0;

    private CheckBox[][] checkBoxes;

    private OnFragmentInteractionListener mListener;

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
    }

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
        }else{
            numRows=1;
            numCols=1;
        }
    }

    //onCreateの直後
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =
                inflater.inflate(R.layout.fragment_seat_grid, container, false);

        //todo 別関数にして何度も呼べるように
        prepareSeatGrid(numRows,numCols,null,view);
        return view;
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

    //縦スクロールされた
    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int y, int oldy) {
        int id = scrollView.getId();
        switch (id){
            case R.id.ScrollView_ForGrid:
                //行数表示のスクロールを動かす
                LockableScrollView lockableScrollView =
                        getView().findViewById(R.id.ScrollView_ForRowNum);
                lockableScrollView.setScrollY(y);
                break;
            default:
                break;
        }
    }

    //横スクロールされた
    @Override
    public void onScrollChanged(ObservableHorizontalScrollView scrollView, int x, int oldx)
    {
        int id = scrollView.getId();
        switch (id)
        {
            case R.id.HorizontalScrollView_ForGrid:
                //列数表示のスクロールを動かす
                LockableHorizontalScrollView lockableHorizontalScrollView=
                        getView().findViewById(R.id.Horizontal_ForColNum);
                lockableHorizontalScrollView.setScrollX(x);
                break;
                default:
                    break;
        }
    }

    //設定値で座席表を作成する関数
    public void prepareSeatGrid(int rows,int cols,Boolean[][] seatState,View view)
    {
        numRows = rows;
        numCols = cols;

        //すべてチェック
        if ( seatState == null)
        {
            seatState = new Boolean[numRows][numCols];
            for(int i=0;i<numRows;i++){
                for(int j=0;j<numCols;j++){
                    seatState[i][j] = true;
                }
            }
        }

        //todo これでいいか？
        if(view == null){
            view = getView();
        }

        // チェックボックス用意
        checkBoxes = new CheckBox[numRows][numCols]; //1次元に？
        CheckBox checkBox;
        for (int i=0;i<numRows;i++) {
            for (int j=0;j<numCols;j++) {
                checkBox = new CheckBox(getActivity());
                //nullで無効、真偽値があればそれに従う
                if(seatState[i][j]==null){
                    checkBox.setChecked(false);
                    checkBox.setEnabled(false);
                }else{
                    checkBox.setChecked(seatState[i][j].booleanValue());
                    checkBox.setEnabled(true);
                }
                checkBoxes[i][j]=checkBox;
            }
        }
        checkBox=null;
        //GridLayoutにチェックボックスを追加
        GridLayout gridLayout =
                (GridLayout) view.findViewById(R.id.GridLayout_checkboxesContainer);
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(numCols);

        //リソースから
        final int GridSize = (int)(MyUtil_ForDpAndPx.convertDp2Px(
                getResources().getInteger(R.integer.GridSize),
                getActivity()
        ) + 0.5);

        final int Margin=(int)(MyUtil_ForDpAndPx.convertDp2Px(
                getResources().getInteger(R.integer.GridMargin),
                getActivity()
        ) + 0.5);


        //チェックボックスを追加
        for(int i=0;i<numRows;i++) {
            for (int j=0;j<numCols;j++) {
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = GridSize;
                params.height = GridSize;
                params.setMargins(Margin,Margin,Margin,Margin);
                gridLayout.addView(checkBoxes[i][j],params);
            }
        }


        //行数表示部の設定
        LockableScrollView VerticalScroll =
                view.findViewById(R.id.ScrollView_ForRowNum);
        VerticalScroll.setScrollingEnabled(false);
        VerticalScroll.setVerticalScrollBarEnabled(false);
        //LinearLayoutに行数表示
        int size = GridSize+Margin*2;
        LinearLayout linearLayout_row = view.findViewById(R.id.LinearLayout_ForRowNums);
        linearLayout_row.removeAllViews();
        for(int i=0;i<numRows;i++)
        {
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,size);
            layoutParams.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
            TextView textView = new TextView(getActivity());
            textView.setText(String.valueOf(i+1));
            textView.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
            linearLayout_row.addView(textView,layoutParams);
        }

        //列数表示部の設定
        LockableHorizontalScrollView HorizontalScroll =
                view.findViewById(R.id.Horizontal_ForColNum);
        HorizontalScroll.setScrollingEnabled(false);
        HorizontalScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout linearLayout_col =
                view.findViewById(R.id.LinearLayout_ForColNums);
        linearLayout_col.removeAllViews();
        for(int i=0;i<numCols;i++){
            LinearLayout.LayoutParams layoutParams=
                    new LinearLayout.LayoutParams(size,LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            TextView textView = new TextView(getActivity());
            textView.setText(String.valueOf(i+1));
            textView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            linearLayout_col.addView(textView,layoutParams);
        }


        //スクロール通知を受け取る設定
        ObservableScrollView observableScrollView=
                view.findViewById(R.id.ScrollView_ForGrid);
        observableScrollView.setOnScrollViewListener(this);

        ObservableHorizontalScrollView observableHorizontalScrollView=
                view.findViewById(R.id.HorizontalScrollView_ForGrid);
        observableHorizontalScrollView.setOnScrollViewListener(this);
    }

    //チェックボックスをすべてチェックつけたり外したり(無効なもの以外)
    public void setIsCheckedAll(boolean isChecked)
    {
        for(int i=0;i<numRows;i++){
            for(int j=0;j<numCols;j++){
                if(checkBoxes[i][j].isEnabled() == false)continue;
                checkBoxes[i][j].setChecked(isChecked);
            }
        }
    }

    //1つのチェックを操作
    public Boolean setIsChecked(int row,int column,boolean isChecked)
    {
        if(!(0<=row&&row<numRows && 0<=column&&column<numCols))return false;
        if(checkBoxes[row][column].isEnabled() == false)return false;

        checkBoxes[row][column].setChecked(isChecked);
        return true;
    }
    


    //チェックボックスの状態を返す 無効ならnull
    public Boolean[][] getIsCheckedAll()
    {
        Boolean[][] ret = new Boolean[numRows][numRows];
        for(int i=0;i<numRows;i++){
            for(int j=0;j<numCols;j++){
                if(checkBoxes[i][j].isEnabled()==true)
                {
                    ret[i][j] = checkBoxes[i][j].isChecked();
                }
                else
                {
                    ret[i][j]=null;
                }

            }
        }
        return ret;
    }
}
