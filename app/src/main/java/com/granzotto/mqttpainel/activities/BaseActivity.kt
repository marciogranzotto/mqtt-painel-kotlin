package com.granzotto.mqttpainel.activities

import android.support.v7.app.AppCompatActivity
import com.granzotto.mqttpainel.views.CustomProgressDialog

/**
 * Created by marciogranzotto on 21/01/17.
 */
open class BaseActivity : AppCompatActivity() {

    private var progressDialog: CustomProgressDialog? = null

    fun showProgressDialog() {
        progressDialog = CustomProgressDialog.show(this)
    }

    fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

}