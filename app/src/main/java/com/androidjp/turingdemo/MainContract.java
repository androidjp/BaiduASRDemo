package com.androidjp.turingdemo;

import android.os.Bundle;

/**
 * Created by androidjp on 2017/3/9.
 */

public class MainContract {

    interface View{
        void showText(String text);
        void showBtnText(String text);
    }

    interface Presenter{
         void startYuYin();
         void stopAndDeal();
        void cancelYuYin();
        void toggleYuYin();
        void getIntentBundle(Bundle bundle);
    }
}
