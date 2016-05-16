package com.granzotto.mqttpainel.views

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.ViewGroup
import android.widget.LinearLayout

import com.granzotto.mqttpainel.R

class CustomProgressDialog(context: Context) : Dialog(context, R.style.ProgressDialog) {
    companion object {

        @JvmOverloads fun show(context: Context, title: CharSequence? = null,
                               message: CharSequence? = null, indeterminate: Boolean = false,
                               cancelable: Boolean = false, cancelListener: DialogInterface.OnCancelListener? = null): CustomProgressDialog {
            val dialog = CustomProgressDialog(context)
            dialog.setTitle(title)
            dialog.setCancelable(cancelable)
            dialog.setOnCancelListener(cancelListener)
            dialog.setContentView(R.layout.dialog_layout)
            val roundContainer = dialog.findViewById(R.id.roundContainer) as LinearLayout

            val progressWheel = CustomProgressWheel(context)

            /* The next line will add the ProgressBar to the dialog. */
            roundContainer.addView(progressWheel, ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT))
            dialog.show()

            return dialog
        }
    }

}
