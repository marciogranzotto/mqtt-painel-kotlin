package com.granzotto.mqttpainel.views

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet

import com.granzotto.mqttpainel.R
import com.granzotto.mqttpainel.utils.ImageHelper
import com.pnikosis.materialishprogress.ProgressWheel

class CustomProgressWheel : ProgressWheel {

    constructor(context: Context) : super(context) {
        setUp(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setUp(context)
    }

    fun setUp(context: Context) {
        spin()
        circleRadius = ImageHelper.dipTOpx(60, context)
        barColor = ContextCompat.getColor(context, R.color.colorPrimary)
        barWidth = ImageHelper.dipTOpx(3, context)
    }
}