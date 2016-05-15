package com.granzotto.mqttpainel.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.granzotto.mqttpainel.R
import com.granzotto.mqttpainel.utils.ConnectionManager
import com.granzotto.mqttpainel.utils.MyConstants
import com.granzotto.mqttpainel.views.CustomProgressDialog
import com.pawegio.kandroid.longToast
import com.pawegio.kandroid.textWatcher
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    companion object {
        val SERVER_URL = "tcp://casa-granzotto.ddns.net:1883"
    }

    private var serverUrl: String? = ""
    private var serverPort: String? = ""
    private var progressDialog: CustomProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpTextWatchers()
        connectButton.setOnClickListener { v -> connect() }

        tryToReconnect()
    }

    private fun tryToReconnect() {
        progressDialog = CustomProgressDialog.show(this)

        val prefs = getSharedPreferences(MyConstants.SHARED_PREFERENCES, MODE_PRIVATE)
        serverUrl = prefs.getString(MyConstants.SERVER_URL, "")
        etServer.setText(serverUrl)
        serverPort = prefs.getString(MyConstants.SERVER_PORT, "")
        etPort.setText(serverPort)
        etUser.setText(prefs.getString(MyConstants.SERVER_USER, ""))
        etPassword.setText(prefs.getString(MyConstants.SERVER_USER_PASSWORD, ""))

        if (!serverUrl.isNullOrBlank() && !serverPort.isNullOrBlank())
            connect()
        else
            progressDialog?.dismiss()
    }


    private fun setUpTextWatchers() {
        etServer.textWatcher {
            afterTextChanged { text ->
                serverUrl = text.toString().trim()
                shouldEnableButton()
            }
        }
        etPort.textWatcher {
            afterTextChanged { text ->
                serverPort = text.toString().trim()
                shouldEnableButton()
            }
        }
    }

    private fun shouldEnableButton() {
        connectButton.isEnabled = !serverUrl.isNullOrEmpty() && !serverPort.isNullOrEmpty()
    }

    private fun connect() {
        val userName = etUser.text.toString()
        val password = etPassword.text.toString()
        ConnectionManager.setUp(serverUrl, serverPort, userName, password)
        ConnectionManager.connectionListener = object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                val prefs = getSharedPreferences(MyConstants.SHARED_PREFERENCES, MODE_PRIVATE)
                val editor = prefs.edit();
                editor.putString(MyConstants.SERVER_URL, serverUrl)
                editor.putString(MyConstants.SERVER_PORT, serverPort)
                editor.putString(MyConstants.SERVER_USER, userName)
                editor.putString(MyConstants.SERVER_USER_PASSWORD, password)
                editor.commit()
                toast("Connected")
                progressDialog?.dismiss()
                startActivity<DashboardActivity>()
                finish()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                longToast("Error connecting")
                exception?.printStackTrace()
                progressDialog?.dismiss()
            }

        }
        ConnectionManager.connect(applicationContext)
    }
}