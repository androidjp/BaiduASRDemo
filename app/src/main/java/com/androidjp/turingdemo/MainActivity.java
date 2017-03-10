package com.androidjp.turingdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidjp.lib_baidu_asr.BaiduASR;
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
    private boolean isDoingAnimation = false;


    /// Presenter对象
    private MainContract.Presenter mPresenter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mPresenter = new MainPresenter(this,this);
        btnSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.toggleYuYin();
            }

        });
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
