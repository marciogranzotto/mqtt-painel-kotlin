package com.granzotto.mqttpainel.presenters

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.granzotto.mqttpainel.activities.MainActivity
import com.granzotto.mqttpainel.models.ConnectionObj
import com.granzotto.mqttpainel.utils.ConnectionListener
import com.granzotto.mqttpainel.utils.ConnectionManager
import com.granzotto.mqttpainel.utils.MyConstants
import com.granzotto.mqttpainel.utils.extensions.flags
import com.pawegio.kandroid.longToast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

/**
 * Created by marciogranzotto on 21/01/17.
 */
class MainPresenter(var view: MainActivity?) {

    companion object {
        val SERVER_URL = "tcp://casa-granzotto.ddns.net:1883"
    }

    private var serverUrl: String? = null
    private var serverPort: String? = null
    private var user: String? = null
    private var password: String? = null

    private var oldConnectObject: ConnectionObj? = null

    fun onCreate() {
        getDataFromSharedPreferences()
    }

    fun onDestroy() {
        view = null
    }

    fun onConnectButtonClicked() {
        connect()
    }

    private fun shouldEnableButton() {
        view?.connectButton?.isEnabled = !serverUrl.isNullOrEmpty() && !serverPort.isNullOrEmpty()
    }

    private fun getDataFromSharedPreferences() {
        view?.showProgressDialog()

        val prefs = view?.getSharedPreferences(MyConstants.SHARED_PREFERENCES, AppCompatActivity.MODE_PRIVATE)
        serverUrl = prefs?.getString(MyConstants.SERVER_URL, null) ?: oldConnectObject?.serverUrl
        view?.etServer?.setText(serverUrl)
        serverPort = prefs?.getString(MyConstants.SERVER_PORT, null) ?: oldConnectObject?.serverPort
        view?.etPort?.setText(serverPort ?: "1883")
        user = prefs?.getString(MyConstants.SERVER_USER, null) ?: oldConnectObject?.user
        view?.etUser?.setText(user)
        password = prefs?.getString(MyConstants.SERVER_USER_PASSWORD, null)
                ?: oldConnectObject?.password
        view?.etPassword?.setText(password)

        if (oldConnectObject == null && !serverUrl.isNullOrBlank() && !serverPort.isNullOrBlank())
            connect()
        else
            view?.dismissProgressDialog()
    }

    fun onPortTextChanged(text: String) {
        serverPort = text.trim()
        shouldEnableButton()
    }

    fun onServerTextChanged(text: String) {
        serverUrl = text.trim()
        shouldEnableButton()
    }

    private fun connect() {
        val userName = view?.etUser?.text.toString()
        val password = view?.etPassword?.text.toString()
        ConnectionManager.setUp(serverUrl, serverPort, userName, password)
        ConnectionManager.connectionListener = object: ConnectionListener {
            override fun onConnected() {
                saveDataToSharedPreferences(userName, password)
                view?.toast("Connected")
                view?.dismissProgressDialog()
                view?.goToDashboardScreen()
            }

            override fun onConnectionError(exception: Throwable?) {
                view?.longToast("Error connecting")
                exception?.printStackTrace()
                view?.dismissProgressDialog()
            }

            override fun onConnectionLost() {
                showConnectionLostDialog()
            }
        }
        if (view == null) return
        ConnectionManager.connect(view!!.applicationContext)
    }

    private fun saveDataToSharedPreferences(userName: String?, password: String?) {
        val prefs = view?.getSharedPreferences(MyConstants.SHARED_PREFERENCES, AppCompatActivity.MODE_PRIVATE)
        val editor = prefs?.edit()
        editor?.putString(MyConstants.SERVER_URL, serverUrl)
        editor?.putString(MyConstants.SERVER_PORT, serverPort)
        editor?.putString(MyConstants.SERVER_USER, userName)
        editor?.putString(MyConstants.SERVER_USER_PASSWORD, password)
        editor?.apply()
    }

    fun onIntentExtras(parcelable: ConnectionObj?) {
        oldConnectObject = parcelable
    }

    private fun showConnectionLostDialog() {
        val activity = view ?: return
        val alert = view?.alert("Connection Lost", "Do you want to reconnect to the broker?") {
            positiveButton("Yes") { ConnectionManager.connect(activity) }
            negativeButton("No") {
                val flags = flags(Intent.FLAG_ACTIVITY_NEW_TASK, Intent.FLAG_ACTIVITY_CLEAR_TOP)
                val intent = Intent(activity, MainActivity::class.java)
                intent.flags = flags
                activity.startActivity(intent)
            }
        }
        try {
            alert?.show()
        } catch (e: Exception) {
            e.printStackTrace()
            activity.longToast("Connection lost!")
        }
    }

}