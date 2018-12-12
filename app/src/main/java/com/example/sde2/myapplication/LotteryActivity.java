package com.example.sde2.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LotteryActivity extends AppCompatActivity
    implements SeatGridFragment.OnFragmentInteractionListener,
        View.OnClickListener
{

    private long mGridID = -1;
    private final String KEY_GRID_ID = "GridID";

    private final String TAG_SEAT_GRID = "SeatGridFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);
        //引数取得
        mGridID = getIntent().getLongExtra(KEY_GRID_ID,-1);

        //フラグメントに渡すための引数
        Bundle bundle = new Bundle();
        bundle.putLong(SeatGridFragment.ARG_GridID,mGridID);

        //フラグメント実体を生成
        SeatGridFragment seatGridFragment = new SeatGridFragment();
        seatGridFragment.setArguments(bundle);
        //フラグメントセット
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.FrameLayout_SeatGridContainer,seatGridFragment,TAG_SEAT_GRID)
                .commit();

        //下部のボタンにリスナー（this）をセット
        Button button = (Button)findViewById(R.id.Button_ManualSetting);
        button.setOnClickListener(this);

        button = (Button)findViewById(R.id.Button_StartLottery);
        button.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        //GridIDを保存しておく
        savedInstanceState.putLong(KEY_GRID_ID,mGridID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        //GridIDを復元する
        String key_GridID = getResources().getString(R.string.IntentExtra_GridID);
        mGridID = savedInstanceState.getLong(key_GridID);
    }


    @Override
    public void CoundNotFetchFromDatabase(SeatGridFragment fragment){
        throw new RuntimeException(getApplicationContext().toString()
            + "Fetching SeatState Error.");
    }

    @Override
    public void onClick(View view){
        final int id = view.getId();
        switch (id)
        {
            case R.id.Button_ManualSetting:
                //todo 手動設定開始
                break;

            case R.id.Button_StartLottery:
                //todo くじ引き開始
                break;
        }
    }

}
