package com.granzotto.mqttpainel.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.granzotto.mqttpainel.BuildConfig;

public class ImageHelper {

    /**
     * Converte um número em dip (ou dp) em pixels
     *
     * @param dip
     * @param context
     * @return int pixels
     */
    public static int dipTOpx(int dip, Context context) {
        int px;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        px = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, displayMetrics));
        if (BuildConfig.DEBUG)
            Log.d("dipTOpx", dip + "dp is " + px + "px on this screen with density " + displayMetrics.densityDpi);
        return px;
    }

}
