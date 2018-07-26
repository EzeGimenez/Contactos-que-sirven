package com.visoft.jobfinder.Util;

import android.content.Context;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

public class ErrorAnimator {

    public static void shakeError(Context context, View view) {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));

        view.startAnimation(shake);
    }
}
