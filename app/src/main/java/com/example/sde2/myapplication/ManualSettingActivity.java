package com.example.sde2.myapplication;

import android.app.Dialog;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ManualSettingActivity extends AppCompatActivity
implements
        View.OnClickListener,
        SeatGridFragment.OnFragmentInteractionListener
{

    public static final String ARG_GRID_ID = "param1";

    private long mGridID = -1;

    private final String KEY_GRID_ID = "GridID";

    private final String TAG_SEATGRID = "SeatGridFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_setting);

        //引数取得
        mGridID = getIntent().getLongExtra(ARG_GRID_ID,-1);

        //フラグメントへの引数
        Bundle bundle = new Bundle();
        bundle.putLong(SeatGridFragment.ARG_GridID,mGridID);

        //フラグメント生成
        TextBoxGridFragment fragment = new TextBoxGridFragment();
        fragment.setArguments(bundle);
        //フレームレイアウトにセット
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.FrameLayout_SeatGridContainer,fragment,TAG_SEATGRID)
                .commit();

        //下部のボタンのセット
        Button button =(Button) findViewById(R.id.Button_OK);
        button.setOnClickListener(this);

        button = (Button)findViewById(R.id.Button_Cancel);
        button.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_GRID_ID,mGridID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mGridID = savedInstanceState.getLong(KEY_GRID_ID,-1);
    }

    @Override
    public void onClick(View v) {
        if(v instanceof Button)
            MyUtil_ForButton.disableButtonForMillisecs((Button)v,1000);


        final int id = v.getId();
        switch (id){
            //決定ボタン　重複確かめてだめならエラー
            case R.id.Button_OK:

                //フラグメント取得
                TextBoxGridFragment fragment =
                        (TextBoxGridFragment)
                                getSupportFragmentManager()
                                .findFragmentByTag(TAG_SEATGRID);
                if(fragment == null)
                    break;

                //重複チェック
                final boolean NoDuplication = fragment.checkSpinners();
                if(NoDuplication == false){
                    //重複があればエラー表示して何もしない
                    new AlertDialog.Builder(this)
                            .setTitle(getResources()
                                    .getString(R.string.AlertDialogTitle_HasDuplication))
                            .setMessage(getResources()
                                    .getString(R.string.AlertDialogMessage_HasDuplication))
                            .setCancelable(true)
                            .show();
                    break;
                }

                //その内容でDB更新　取り出すんめんどいんでfragmentで実装
                if(fragment.updateDB() == false)
                    break;

                setResult(RESULT_OK);
                finish();
                break;

            case R.id.Button_Cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    @Override
    public void CouldNotFetchFromDatabase(SeatGridFragment fragment) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
