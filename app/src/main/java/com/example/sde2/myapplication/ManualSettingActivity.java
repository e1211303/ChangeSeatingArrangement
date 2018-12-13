package com.example.sde2.myapplication;

import android.os.PersistableBundle;
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
        SeatGridFragment fragment = new SeatGridFragment();
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
        final int id = v.getId();
        switch (id){
            case R.id.Button_OK:
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
