package com.androidjp.lib_baidu_asr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.util.Log;

import com.androidjp.lib_baidu_asr.asr.ASRManager;

/**
 * Baidu 的 语音识别API 装饰类，（语音识别：Automatic Speech Recognition）
 *
 * 兼容性：
 *  SO: Android 2.3（API Level 9） 以上
 *  构架：支持 ARM平台（只提供armeabi架构的动态库， 可兼容其他架构）
 *  硬件要求：有麦克风
 *  网络：WIFI、移动网络（2~4G）
 *
 * Created by androidjp on 2017/3/8.
 */

public class BaiduASR {
    private static final String TAG = "BaiduASR";
    /**
     * 打开 语音识别的相关设置界面
     * @param context 上下文（Activity）
     */
    public static void openSettings(Context context){
        Log.i(TAG, "openSettings()");
        Intent intent = new Intent("com.baidu.speech.asr.demo.setting");
        context.startActivity(intent);
    }

    /**
     * 开始语音录入和识别
     * @param activity 上下文 Activity
     * @param listener 回调
     */
    public static void startASR(Activity activity, RecognitionListener listener){
        Log.i(TAG, "startASR()");
        ASRManager.getInstance().init(activity,listener)
                .start();
    }

    /**
     * 停止语音录入
     */
    public static void stopASR(){
        Log.i(TAG, "stopASR()");
        ASRManager.getInstance().stop();
    }

    /**
     * 取消语音识别
     */
    public static void cancelASR(){
        Log.i(TAG, "cancelASR()");
        ASRManager.getInstance().cancel();
    }

    /**
     * 销毁相关对象
     */
    public static void release(){
        Log.i(TAG, "release()");
        ASRManager.getInstance().release();
    }

}
