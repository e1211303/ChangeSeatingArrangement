package com.example.sde2.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InputRowAndColumnFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputRowAndColumnFragment extends Fragment
implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters


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
    //こちらが呼び出して、情報ををActivityに渡してあげる。向こうは実装する必要がある
    //関数名被りしてもいいように、引数に自分のクラス型を入れることにしてみた
    public interface OnFragmentInteractionListener {
        void onSetRowsAndColumns(InputRowAndColumnFragment fragment,int rows, int columns);
        void onGoToNext(InputRowAndColumnFragment fragment);
    }

    public InputRowAndColumnFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment InputRowAndColumnFragment.
     */
    // 生成するために使われるファクトリメソッド。引数なし
    public static InputRowAndColumnFragment newInstance() {
        InputRowAndColumnFragment fragment = new InputRowAndColumnFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //生成されたときの処理。保存した引数取り出しとか
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_input_row_and_column, container, false);

        //ボタンのリスナーをセット
        Button button_OK = view.findViewById(R.id.button_OK);
        Button button_Next = view.findViewById(R.id.button_next);
        button_OK.setOnClickListener(this);
        button_Next.setOnClickListener(this);

        //スピナーの項目を用意
        //アダプタがintを受け付けてくれないからIntegerに変える。いい方法がわからない。
        int items[] = getResources().getIntArray(R.array.RowAndColumn_items);
        int size = items.length;
        Integer[] Items_Rows = new Integer[size];
        Integer[] Items_Cols = new Integer[size];
        for(int i=0;i<size;i++){
            Items_Rows[i] = items[i];
            Items_Cols[i] = items[i];
        }
        //項目をセットしたアダプタを用意
        ArrayAdapter<Integer>arrayAdapter_Rows =
                new ArrayAdapter<Integer>(getContext(),android.R.layout.simple_spinner_item,Items_Rows);
        ArrayAdapter<Integer> arrayAdapter_Cols=
                new ArrayAdapter<Integer>(getContext(),android.R.layout.simple_spinner_item,Items_Cols);
        //レイアウトを設定
        arrayAdapter_Rows.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter_Cols.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //スピナーにアダプタをセット
        Spinner spinner_Rows = view.findViewById(R.id.spinner_row);
        Spinner spinner_Cols = view.findViewById(R.id.spinner_column);
        spinner_Rows.setAdapter(arrayAdapter_Rows);
        spinner_Cols.setAdapter(arrayAdapter_Cols);
        return view;
    }

    // TODO: 押されたボタンに応じてActivityにコールバック
    public void onClick(View view) {

        //連打禁止
        MyUtil_ForButton.disableButtonForMillisecs(
                (Button)view,
                1000);

        int id = view.getId();
        switch (id){
            case R.id.button_OK:
                if(mListener == null)break;
                //スピナーの値を読んで返す
                Spinner spinner_rows = getActivity().findViewById(R.id.spinner_row);
                Spinner spinner_cols = getActivity().findViewById(R.id.spinner_column);
                int rows = (int)spinner_rows.getSelectedItem();
                int cols = (int)spinner_cols.getSelectedItem();
                mListener.onSetRowsAndColumns(this,rows,cols);
                break;

            case R.id.button_next:
                //決定ボタンが押されたことを通知
                mListener.onGoToNext(this);

            default:
                    break;
        }

    }

    //Activity保存。コールバック用のインターフェースを実装したものだけ
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


}
