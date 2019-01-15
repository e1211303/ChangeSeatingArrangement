package com.example.sde2.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CheckBoxGridFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CheckBoxGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckBoxGridFragment extends Fragment
implements ObservableScrollView.ScrollViewListener,
        ObservableHorizontalScrollView.ScrollViewListener {

    //席状態を表す
    public enum SeatState{
        Normal_Checked,
        Normal_Unchecked,
        Scoped_Checked,
        Scoped_Unchecked,
        Empty
    }

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_NUM_ROWS = "param1";
    public static final String ARG_NUM_COLS = "param2";

    //行数列数記憶
    private int mNumRows;
    private int mNumCols;


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

    public CheckBoxGridFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param numRows Parameter 1.
     * @param numCols Parameter 2.
     * @return A new instance of fragment CheckBoxGridFragment.
     */
    public static CheckBoxGridFragment newInstance(int numRows, int numCols) {
        // 引数をバンドルしたインスタンスを生成し返す
        CheckBoxGridFragment fragment = new CheckBoxGridFragment();
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
            mNumRows = getArguments().getInt(ARG_NUM_ROWS);
            mNumCols = getArguments().getInt(ARG_NUM_COLS);
        }
    }

    //onCreateの直後
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =
                inflater.inflate(R.layout.fragment_seat_grid, container, false);

        //碁盤目状のチェックボックス用意
        prepareCheckBoxGrid(mNumRows, mNumCols,null);
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
    public boolean prepareCheckBoxGrid(final int rows, final int cols, SeatState[] seatStates)
    {
        if(rows < 1 || cols < 1)
            return false;

        //座席数
        final int numOfSeats = rows*cols;

        //外部から呼ばれることもできるため
        if ( seatStates == null)
        {
            //すべて普通席
            seatStates = new SeatState[numOfSeats];
            for(int i=0;i<numOfSeats;i++){
                seatStates[i] = SeatState.Normal_Checked;
            }
        }

        //配列大きさ確認
        if(seatStates.length != numOfSeats)
            return false;

        //メンバに記憶しておく
        mNumRows = rows;
        mNumCols = cols;

        //各種Viewの操作のため
        View view = getView();

        // 席状態をもとに表示するチェックボックスを用意
        CheckBox[] checkBoxes = new CheckBox[numOfSeats];
        for(int i=0;i<numOfSeats;++i){
            checkBoxes[i] = createCheckBoxBySeatState(seatStates[i]);
        }

        //GridLayoutにチェックボックスを追加
        GridLayout gridLayout =
                (GridLayout) view.findViewById(R.id.GridLayout_ViewContainer);
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(cols);

        //グリッドの大きさなど
        final int GridSize = (int)(MyUtil_ForDpAndPx.convertDp2Px(
                getResources().getInteger(R.integer.GridSize_ForCheckBox),
                getActivity()
        ) + 0.5);

        final int Margin=(int)(MyUtil_ForDpAndPx.convertDp2Px(
                getResources().getInteger(R.integer.GridMargin),
                getActivity()
        ) + 0.5);


        //チェックボックスを追加
        for(int i=0;i<numOfSeats;++i){
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridSize;
            params.height = GridSize;
            params.setMargins(Margin,Margin,Margin,Margin);
            gridLayout.addView(checkBoxes[i]);
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
        for(int i = 0; i< mNumRows; i++)
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

        for(int i = 0; i< mNumCols; i++){
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

        return true;
    }

    //指定位置のチェックボックス取得
    private CheckBox getCheckBoxAt(final int i,final int j){
        View view = getView();
        if(view == null)
            return null;
        GridLayout gridLayout = (GridLayout)view.findViewById(R.id.GridLayout_ViewContainer);
        if(gridLayout instanceof GridLayout == false)
            return null;

        //todo チェックボックス取得 すぐ下を参考に
        final int cols = gridLayout.getColumnCount();
        final int numOfSeats = gridLayout.getChildCount();

        CheckBox checkBox = (CheckBox)gridLayout.getChildAt(i*cols+j);
        if(checkBox instanceof CheckBox == false)
            return null;

        return checkBox;
    }

    //チェックボックスをすべてチェックつけたり外したり (無効なもの以外)
    public boolean setIsCheckedAll(boolean isChecked)
    {
        View view = getView();
        if(view == null)
            return false;
        GridLayout gridLayout = (GridLayout)view.findViewById(R.id.GridLayout_ViewContainer);
        if(gridLayout instanceof GridLayout == false)
            return false;

        final int numOfSeats = gridLayout.getChildCount();
        for(int i = 0; i< mNumRows; i++){
            //子ビューであるCheckBoxを取得
            CheckBox checkBox = (CheckBox) gridLayout.getChildAt(i);
            if(checkBox instanceof CheckBox == false)
                continue;

            //無効なものは触らない
            if(checkBox.isEnabled()==false)
                continue;

            //チェックつけ外し
            checkBox.setChecked(isChecked);
        }

        return true;
    }

    //1つのチェックを操作
    public boolean setIsChecked(int row,int column,boolean isChecked)
    {
        if(row < 1 || column < 1)
            return false;

        View view = getView();
        if(view == null)
            return false;

        GridLayout gridLayout = (GridLayout)view.findViewById(R.id.GridLayout_ViewContainer);
        if(gridLayout instanceof GridLayout == false)
            return false;

        final int colCount = gridLayout.getColumnCount();
        CheckBox checkBox = (CheckBox) gridLayout.getChildAt(row*colCount+column);
        if(checkBox instanceof CheckBox==false)
            return false;

        //無効なものは触らない
        if(checkBox.isEnabled() == false)
            return false;

        checkBox.setChecked(isChecked);
        return true;
    }
    


    //チェックボックスの状態を返す
    public SeatState[] getSeatStatesAll()
    {
        //GridLayoutから項目数を取得
        View view = getView();
        if(view == null)
            return null;
        GridLayout gridLayout = (GridLayout)view.findViewById(R.id.GridLayout_ViewContainer);
        if(gridLayout instanceof GridLayout == false)
            return null;

        final int numOfSeats = gridLayout.getChildCount();
        final int scopedColor =
                getResources().getColor(R.color.BackGround_ForScopedSeatCheckBox);

        //すべてについて状態を調べる
        SeatState[] retStates = new SeatState[numOfSeats];
        for(int i = 0; i< numOfSeats; i++){
            //CheckBox取得
            CheckBox checkBox = (CheckBox)gridLayout.getChildAt(i);
            if(checkBox instanceof CheckBox)
                return null;    //その時点で中止してみる

            //状態取得
            final boolean isChecked = checkBox.isChecked();
            final boolean isEnabled = checkBox.isEnabled();
            final int backColor = ((ColorDrawable) checkBox.getBackground()).getColor();

            //空席設定の場合
            if(checkBox.isEnabled() == false){
                retStates[i] = SeatState.Empty;
                continue;
            }

            //スコープ設定の場合
            if(backColor == scopedColor){
                //チェック済かどうか
                SeatState state;
                if(isChecked){
                    state = SeatState.Scoped_Checked;
                }else{
                    state = SeatState.Scoped_Unchecked;
                }
                retStates[i] = state;
                continue;
            }

            //あとは普通席
            SeatState state;
            if(isChecked){
                state = SeatState.Normal_Checked;
            }else{
                state = SeatState.Normal_Unchecked;
            }
            retStates[i] = state;
            //continue;
        }
        return retStates;
    }

    //席状態から、適切なチェックボックスを作って返す
    private CheckBox createCheckBoxBySeatState(SeatState seatState){
        CheckBox checkBox = new CheckBox(getActivity());

        final int scopedColor =
                getResources().getColor(R.color.BackGround_ForScopedSeatCheckBox);

        //Scopedは色付き。Emptyは触れない
        switch (seatState){
            case Normal_Checked:
                checkBox.setEnabled(true);
                checkBox.setChecked(true);
                break;

            case Normal_Unchecked:
                checkBox.setEnabled(true);
                checkBox.setChecked(false);
                break;

            case Scoped_Checked:
                checkBox.setEnabled(true);
                checkBox.setChecked(true);
                checkBox.setBackgroundColor(scopedColor);
                break;

            case Scoped_Unchecked:
                checkBox.setEnabled(true);
                checkBox.setChecked(false);
                checkBox.setBackgroundColor(scopedColor);
                break;

            case Empty:
                checkBox.setEnabled(false);
                checkBox.setChecked(false);
                break;
        }

        return checkBox;
    }
}
