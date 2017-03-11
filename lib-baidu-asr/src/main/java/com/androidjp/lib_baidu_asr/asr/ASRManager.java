package com.androidjp.lib_baidu_asr.asr;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.androidjp.lib_baidu_asr.R;
import com.androidjp.lib_baidu_asr.data.Constant;
import com.baidu.speech.VoiceRecognitionService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * 语音识别封装管理类
 * Created by androidjp on 2017/3/8.
 */

public class ASRManager {

    private static final String TAG = "ASRManager";
    ///被封装的SpeechRecognizer对象
    private SpeechRecognizer speechRecognizer;
    private Intent intent;
    private WeakReference<Activity> refContext;

    /// 语音识别相关情况参数
    private static final int REQUEST_UI = 1;

    private long speechEndTime = -1;

    private static class SingletonHolder {
        private static final ASRManager sInstance = new ASRManager();
    }

    public static ASRManager getInstance() {
        return SingletonHolder.sInstance;
    }

    //------------------------------
    public ASRManager init(Activity context, RecognitionListener recognitionListener) {
        Log.i(TAG, "init()");
        ///创建
        refContext = new WeakReference<Activity>(context);
        if (refContext.get() == null)
            return null;
        this.speechRecognizer = SpeechRecognizer.createSpeechRecognizer(refContext.get(), new ComponentName(refContext.get(), VoiceRecognitionService.class));
        if (recognitionListener != null)
            this.speechRecognizer.setRecognitionListener(recognitionListener);
        Log.i(TAG, "init() finished!!");
        return this;
    }

    private void bindParams(Intent intent) {
        if (refContext.get() == null)
            return;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(refContext.get());
        if (sp.getBoolean("tips_sound", true)) {
            intent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
            intent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
            intent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
            intent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
            intent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        }
        if (sp.contains(Constant.EXTRA_INFILE)) {
            String tmp = sp.getString(Constant.EXTRA_INFILE, "").replaceAll(",.*", "").trim();
            intent.putExtra(Constant.EXTRA_INFILE, tmp);
        }
        if (sp.getBoolean(Constant.EXTRA_OUTFILE, false)) {
            intent.putExtra(Constant.EXTRA_OUTFILE, "sdcard/outfile.pcm");
        }
        if (sp.getBoolean(Constant.EXTRA_GRAMMAR, false)) {
            intent.putExtra(Constant.EXTRA_GRAMMAR, "assets:///baidu_speech_grammar.bsg");
        }
        if (sp.contains(Constant.EXTRA_SAMPLE)) {
            String tmp = sp.getString(Constant.EXTRA_SAMPLE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_SAMPLE, Integer.parseInt(tmp));
            }
        }
        if (sp.contains(Constant.EXTRA_LANGUAGE)) {
            String tmp = sp.getString(Constant.EXTRA_LANGUAGE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_LANGUAGE, tmp);
            }
        }
        if (sp.contains(Constant.EXTRA_NLU)) {
            String tmp = sp.getString(Constant.EXTRA_NLU, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_NLU, tmp);
            }
        }

        if (sp.contains(Constant.EXTRA_VAD)) {
            String tmp = sp.getString(Constant.EXTRA_VAD, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_VAD, tmp);
            }
        }
        String prop = null;
        if (sp.contains(Constant.EXTRA_PROP)) {
            String tmp = sp.getString(Constant.EXTRA_PROP, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_PROP, Integer.parseInt(tmp));
                prop = tmp;
            }
        }

        // offline asr
        {
            intent.putExtra(Constant.EXTRA_OFFLINE_ASR_BASE_FILE_PATH, "/sdcard/easr/s_1");
            if (null != prop) {
                int propInt = Integer.parseInt(prop);
                if (propInt == 10060) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_Navi");
                } else if (propInt == 20000) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_InputMethod");
                }
            }
            intent.putExtra(Constant.EXTRA_OFFLINE_SLOT_DATA, buildTestSlotData());
        }
    }

    private String buildTestSlotData() {
        JSONObject slotData = new JSONObject();
        JSONArray name = new JSONArray().put("李涌泉").put("郭下纶");
        JSONArray song = new JSONArray().put("七里香").put("发如雪");
        JSONArray artist = new JSONArray().put("周杰伦").put("李世龙");
        JSONArray app = new JSONArray().put("手机百度").put("百度地图");
        JSONArray usercommand = new JSONArray().put("关灯").put("开门");
        try {
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_NAME, name);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_SONG, song);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_ARTIST, artist);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_APP, app);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_USERCOMMAND, usercommand);
        } catch (JSONException e) {

        }
        return slotData.toString();
    }


    public void start() {
        Log.i(TAG, "start()");
        if (refContext.get() == null)
            return;
        Intent intent = new Intent();
        bindParams(intent);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(refContext.get());
        {

            String args = sp.getString("args", "");
            if (null != args) {
//                print("参数集：" + args);
                intent.putExtra("args", args);
            }
        }
        boolean api = sp.getBoolean("api", false);
        if (api) {
            speechEndTime = -1;
            speechRecognizer.startListening(intent);
        } else {
            intent.setAction("com.baidu.action.RECOGNIZE_SPEECH");
            refContext.get().startActivityForResult(intent, REQUEST_UI);
        }
        Log.i(TAG, "start() finished!!");

//        txtResult.setText("");
    }


    public void stop() {
        Log.i(TAG, "stop()");
        if (speechRecognizer == null) return;
        speechRecognizer.stopListening();
//        print("点击了“说完了”");
        Log.i(TAG, "stop() finished!!");

    }

    public void cancel() {
        Log.i(TAG, "cancel()");
        if (speechRecognizer == null) return;
        speechRecognizer.cancel();
//        print("点击了“取消”");
        Log.i(TAG, "cancel() finished!!");

    }

    public void release() {
        Log.i(TAG, "release()");
        if (speechRecognizer == null) return;
        speechRecognizer.destroy();
        Log.i(TAG, "release() finished!!");

    }

}
