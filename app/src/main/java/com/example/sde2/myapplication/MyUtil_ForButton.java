package com.example.sde2.myapplication;

import android.widget.Button;

import java.util.logging.Handler;

public class MyUtil_ForButton {
    static void disableButtonForMillisecs(final Button button,long milliSeconds){

        button.setEnabled(false);
        button.getHandler().postDelayed(new Runnable() {
            public void run() {
                button.setEnabled(true);
            }
        },milliSeconds);
    }
}
