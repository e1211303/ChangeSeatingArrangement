package com.example.sde2.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InputScopeSettingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InputScopeSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputScopeSettingFragment extends Fragment
implements View.OnClickListener{

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
        void onGoToNext(InputScopeSettingFragment fragment);
    }

    public InputScopeSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment InputScopeSettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InputScopeSettingFragment newInstance() {
        InputScopeSettingFragment fragment = new InputScopeSettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    //todo ボタンのリスナー等
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater
                .inflate(R.layout.fragment_input_using_scope_setting, container, false);

        //次へのボタンのリスナーセット
        Button button_next = view.findViewById(R.id.Button_next);
        button_next.setOnClickListener(this);

        return view;
    }

    // ボタンが押されたとき
    public void onClick(View view) {
        if(view instanceof Button){
            MyUtil_ForButton.disableButtonForMillisecs((Button)view,1000L);
        }

        int id = view.getId();

        switch (id)
        {
            case R.id.Button_next:
                if (mListener==null)break;
                mListener.onGoToNext(this);
                break;
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


}
