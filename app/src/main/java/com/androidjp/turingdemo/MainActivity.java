package com.androidjp.turingdemo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidjp.lib_baidu_asr.BaiduASR;
import com.androidjp.lib_baidu_asr.data.Constant;
import com.skyfishjy.library.RippleBackground;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainContract.View{

    ///控件
    @Bind(R.id.ripple_bg)
    RippleBackground rippleBackground;
    @Bind(R.id.btn_speech)
    Button btnSpeech;
    @Bind(R.id.tv_result)
    TextView tvResult;

    /// Presenter对象
    private MainContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mPresenter = new MainPresenter(this,this);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().remove(Constant.EXTRA_INFILE).commit(); // infile参数用于控制识别一个PCM音频流（或文件），每次进入程序都将该值清楚，以避免体验时没有使用录音的问题


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{
//                    Manifest.permission.RECORD_AUDIO
//            }, /*YOUR_REQUEST_CODE*/0); // requestPermissions是Activity的方法
//        }

        btnSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.toggleYuYin();
            }

        });
    }

    public void gotoSettings(View v){
        BaiduASR.openSettings(this);
    }

    public void gotoDemo(View v){
        startActivity(new Intent(this, DemoActivity.class));
    }

    @Override
    public void showText(String text) {
        this.tvResult.setText(text);
    }

    @Override
    public void showBtnText(String text){
        this.btnSpeech.setText(text);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mPresenter.getIntentBundle(data.getExtras());
        }
    }


    @Override
    protected void onDestroy() {
        BaiduASR.cancelASR();
        super.onDestroy();
    }
}
