package com.granzotto.mqttpainel.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue

import com.granzotto.mqttpainel.BuildConfig

object ImageHelper {

    /**
     * Converte um número em dip (ou dp) em pixels

     * @param dip
     * *
     * @param context
     * *
     * @return int pixels
     */
    fun dipTOpx(dip: Int, context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val px = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip.toFloat(), displayMetrics))
        if (BuildConfig.DEBUG)
            Log.d("dipTOpx", "${dip} dp is ${px}px on this screen with density ${displayMetrics.densityDpi}")
        return px
    }

}
