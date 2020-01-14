package com.ottt.otfm;

import android.graphics.Color;
import android.view.View;

public class BlinkHandler {
    private static View view;
    private static int blinkCount = 0;

    static void blink(View v) {
        view = v;
        blink_on.run();
    }

    private static Runnable blink_on = new Runnable() {

        public void run() {
            view.getBackground().setTint(Color.RED);
            view.postDelayed(blink_off,100);
        }
    };

    private static Runnable blink_off = new Runnable() {
        @Override
        public void run() {
            view.getBackground().setTint(Color.GRAY);
            if (blinkCount < 2) {
                view.postDelayed(blink_on,100); blinkCount++; }
            else {blinkCount = 0;}
        }
    };
}
