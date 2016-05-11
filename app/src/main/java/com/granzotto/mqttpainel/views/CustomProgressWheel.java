package com.granzotto.mqttpainel.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.granzotto.mqttpainel.R;
import com.granzotto.mqttpainel.utils.ImageHelper;
import com.pnikosis.materialishprogress.ProgressWheel;

public class CustomProgressWheel extends ProgressWheel {

    public CustomProgressWheel(Context context) {
        super(context);
        spin();
        setCircleRadius(ImageHelper.dipTOpx(60, context));
        setBarColor(ContextCompat.getColor(context, R.color.colorPrimary));
        setBarWidth(ImageHelper.dipTOpx(3, context));
    }

    public CustomProgressWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
        spin();
        setCircleRadius(ImageHelper.dipTOpx(60, context));
        setBarColor(ContextCompat.getColor(context, R.color.colorPrimary));
        setBarWidth(ImageHelper.dipTOpx(3, context));
    }
}