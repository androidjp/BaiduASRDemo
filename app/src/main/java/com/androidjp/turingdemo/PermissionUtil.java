package com.androidjp.turingdemo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * 动态权限申请工具类（初版）
 * Created by androidjp on 2017/3/12.
 */

public class PermissionUtil {

    public static void checkForPermission(Context context, String[] permissions) {

        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(context, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission(context ,permissions);
            }
        }
    }

    // 提示用户该请求权限的弹出框
    private static  void showDialogTipUserRequestPermission(final Context context, final String[] permissions) {

        new AlertDialog.Builder(context)
                .setTitle("录音权限不可用")
                .setMessage("请开启录音功能\n否则，您将无法正常使用语音识别")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission(context,permissions);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((Activity)context).finish();
                    }
                }).setCancelable(false).show();
    }

    // 开始提交请求权限
    private static  void startRequestPermission(Context context, String[] permissions) {
        ActivityCompat.requestPermissions((Activity) context, permissions, 321);
    }



}
