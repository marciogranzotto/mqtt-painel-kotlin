package com.granzotto.mqttpainel.views;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.granzotto.mqttpainel.R;

public class CustomProgressDialog extends Dialog {

    public static CustomProgressDialog show(Context context) {
        return show(context, null, null);
    }

    public static CustomProgressDialog show(Context context, CharSequence title,
                                            CharSequence message) {
        return show(context, title, message, false);
    }

    public static CustomProgressDialog show(Context context, CharSequence title,
                                            CharSequence message, boolean indeterminate) {
        return show(context, title, message, indeterminate, false, null);
    }

    public static CustomProgressDialog show(Context context, CharSequence title,
                                            CharSequence message, boolean indeterminate, boolean cancelable) {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    public static CustomProgressDialog show(Context context, CharSequence title,
                                            CharSequence message, boolean indeterminate,
                                            boolean cancelable, OnCancelListener cancelListener) {
        CustomProgressDialog dialog = new CustomProgressDialog(context);
        dialog.setTitle(title);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.setContentView(R.layout.dialog_layout);
        LinearLayout roundContainer = (LinearLayout) dialog.findViewById(R.id.roundContainer);

        CustomProgressWheel progressWheel = new CustomProgressWheel(context);

        /* The next line will add the ProgressBar to the dialog. */
        roundContainer.addView(progressWheel, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.show();

        return dialog;
    }

    public CustomProgressDialog(Context context) {
        super(context, R.style.ProgressDialog);
    }

}
