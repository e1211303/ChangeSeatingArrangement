package com.example.sde2.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LotteryActivity extends AppCompatActivity
    implements
        SeatGridFragment.OnFragmentInteractionListener,
        View.OnClickListener
{

    private long mGridID = -1;
    private final String KEY_GRID_ID = "GridID";

    private final String TAG_SEAT_GRID = "SeatGridFragment";

    private final int REQUEST_CODE_MANUAL_SETTING = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);
        //引数取得
        mGridID = getIntent().getLongExtra(KEY_GRID_ID,-1);

        //フラグメントに渡すための引数
        Bundle bundle = new Bundle();
        bundle.putLong(SeatGridFragment.ARG_GridID,mGridID);

        //フラグメントセット
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.FrameLayout_SeatGridContainer,
                        SeatGridFragment.newInstance(mGridID),
                        TAG_SEAT_GRID)
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
    public void CouldNotFetchFromDatabase(SeatGridFragment fragment){
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
                Intent intent = new Intent(getApplication(),ManualSettingActivity.class);
                intent.putExtra(ManualSettingActivity.ARG_GRID_ID,mGridID);
                startActivityForResult(intent,REQUEST_CODE_MANUAL_SETTING);
                break;

            case R.id.Button_StartLottery:
                //todo くじ引き開始
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch(requestCode){
            case REQUEST_CODE_MANUAL_SETTING:
                if(resultCode != RESULT_OK)
                    return;

                //SeatGrid更新
                SeatGridFragment fragment =  (SeatGridFragment)getSupportFragmentManager()
                        .findFragmentByTag(TAG_SEAT_GRID);
                fragment.prepareSeatGrid(mGridID,null);
                break;
        }
    }

}
