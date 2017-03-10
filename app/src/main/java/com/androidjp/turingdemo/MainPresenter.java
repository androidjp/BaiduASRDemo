package com.androidjp.turingdemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import com.androidjp.lib_baidu_asr.BaiduASR;
import com.baidu.speech.VoiceRecognitionService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static android.os.MessageQueue.OnFileDescriptorEventListener.EVENT_ERROR;
import static com.androidjp.lib_baidu_asr.asr.ASRStatus.STATUS_None;
import static com.androidjp.lib_baidu_asr.asr.ASRStatus.STATUS_Ready;
import static com.androidjp.lib_baidu_asr.asr.ASRStatus.STATUS_Recognition;
import static com.androidjp.lib_baidu_asr.asr.ASRStatus.STATUS_Speaking;
import static com.androidjp.lib_baidu_asr.asr.ASRStatus.STATUS_WaitingReady;

/**
 * MainContract.Presenter实现类
 * Created by androidjp on 2017/3/9.
 */

public class MainPresenter implements MainContract.Presenter, RecognitionListener{

    private Context mContext;
    private WeakReference<MainContract.View> mView;
    ///相关参数
    private int status = STATUS_None;
    private long time;
    private long speechEndTime = -1;

    public MainPresenter(Context context, MainContract.View view) {
        mContext = context;
        mView = new WeakReference<MainContract.View>(view);
    }

    @Override
    public void startYuYin() {
        if (mView !=null)
            mView.get().showText("");
        BaiduASR.startASR((Activity)mContext, this);
    }

    @Override
    public void stopAndDeal() {
        BaiduASR.stopASR();
    }

    @Override
    public void cancelYuYin() {
        status = STATUS_None;
        BaiduASR.cancelASR();
    }

    @Override
    public void toggleYuYin() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean api = sp.getBoolean("api", false);
        if (api) {
            switch (status) {
                case STATUS_None:
                    startYuYin();
                    status = STATUS_WaitingReady;
                    if (mView !=null)
                        mView.get().showText("取消");
                    break;
                case STATUS_WaitingReady:
                    cancelYuYin();
                    status = STATUS_None;
                    if (mView !=null)
                        mView.get().showText("开始");
                    break;
                case STATUS_Ready:
                    cancelYuYin();
                    status = STATUS_None;
                    if (mView !=null)
                        mView.get().showText("开始");
                    break;
                case STATUS_Speaking:
                    stopAndDeal();
                    status = STATUS_Recognition;
                    if (mView !=null)
                        mView.get().showText("识别中");
                    break;
                case STATUS_Recognition:
                    cancelYuYin();
                    status = STATUS_None;
                    if (mView !=null)
                        mView.get().showText("开始");
                    break;
            }
        } else {
            startYuYin();
        }
    }

    @Override
    public void getIntentBundle(Bundle bundle) {

    }


    ///----------------------------------------------------------------


    @Override
    public void onReadyForSpeech(Bundle params) {
        status = STATUS_Ready;
    }

    @Override
    public void onBeginningOfSpeech() {
        time = System.currentTimeMillis();
        status = STATUS_Speaking;


    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        speechEndTime = System.currentTimeMillis();
        status = STATUS_Recognition;
        mView.get().showBtnText("识别中");
    }

    @Override
    public void onError(int error) {
        time = 0;
        status = STATUS_None;
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        sb.append(":" + error);
//        print("识别失败：" + sb.toString());
        mView.get().showBtnText("开始");
    }

    @Override
    public void onResults(Bundle results) {
        long end2finish = System.currentTimeMillis() - speechEndTime;
        status = STATUS_None;
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//        print("识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
        String json_res = results.getString("origin_result");
//        try {
//            print("origin_result=\n" + new JSONObject(json_res).toString(4));
//        } catch (Exception e) {
//            print("origin_result=[warning: bad json]\n" + json_res);
//        }
        mView.get().showBtnText("开始");
        String strEnd2Finish = "";
        if (end2finish < 60 * 1000) {
            strEnd2Finish = "(waited " + end2finish + "ms)";
        }
        mView.get().showText(nbest.get(0) + strEnd2Finish);
        time = 0;
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest.size() > 0) {
//            print("~临时识别结果：" + Arrays.toString(nbest.toArray(new String[0])));
            mView.get().showText(nbest.get(0));
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        switch (eventType) {
            case EVENT_ERROR:
                String reason = params.get("reason") + "";
//                print("EVENT_ERROR, " + reason);
                break;
            case VoiceRecognitionService.EVENT_ENGINE_SWITCH:
                int type = params.getInt("engine_type");
//                print("*引擎切换至" + (type == 0 ? "在线" : "离线"));
                break;
        }
    }
}
