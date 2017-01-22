package com.granzotto.mqttpainel.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import com.granzotto.mqttpainel.BuildConfig
import com.granzotto.mqttpainel.activities.MainActivity
import com.granzotto.mqttpainel.utils.extensions.flags
import com.pawegio.kandroid.d
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.longToast
import java.util.*

object ConnectionManager {

    private var serverUrl: String? = ""
    private var serverPort: String? = ""
    private var userName: String? = ""
    private var userPassword: String? = ""

    var client: MqttAndroidClient? = null
    var connectionListener: IMqttActionListener? = null
    var listeners: HashMap<String, MessageReceivedListener> = HashMap()

    private var context: Context? = null

    fun setUp(serverUrl: String?, serverPort: String?, userName: String?, userPassword: String?) {
        this.serverUrl = serverUrl;
        this.serverPort = serverPort;
        this.userName = userName;
        this.userPassword = userPassword;
    }

    fun connect(appContext: Context) {
        this.context = appContext

        if (serverUrl.isNullOrBlank() || serverPort.isNullOrBlank())
            return

        serverUrl = if (!serverUrl.isNullOrBlank() && serverUrl!!.contains("//")) serverUrl!! else
            "tcp://" + serverUrl

        val url: String = "${serverUrl}:${serverPort}";

        val clientID = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID) + "-android";

        if (BuildConfig.DEBUG)
            d("clientID: ${clientID}")

        client = MqttAndroidClient(appContext, url, clientID);
        client?.registerResources(appContext)
        val options = MqttConnectOptions();
        options.userName = userName
        options.password = userPassword?.toCharArray()
        client?.connect(options, null, connectionListener)

        client?.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                listeners.forEach {
                    it.value.messageReceived(topic, message)
                }
            }

            override fun connectionLost(cause: Throwable?) {
                if (BuildConfig.DEBUG)
                    Log.e("ConnectionManager", "Connection lost!", cause)
                showConnectionLostDialog()
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                if (BuildConfig.DEBUG)
                    d("deliveryComplete: \n${token}")
            }

        })
    }

    fun disconnect() {
        client?.disconnect()
    }

    private fun showConnectionLostDialog() {
        val alert = context?.alert("Connection Lost", "Do you want to reconnect to the broker?") {
            positiveButton("Yes") { connect(context!!) }
            negativeButton("No") {
                val flags = flags(Intent.FLAG_ACTIVITY_NEW_TASK, Intent.FLAG_ACTIVITY_CLEAR_TOP)
                val intent = Intent(context!!, MainActivity::class.java)
                intent.flags = flags
                context?.startActivity(intent)
            }
        }
        try {
            alert?.show();
        } catch (e: Exception) {
            e.printStackTrace()
            context?.longToast("Connection lost!")
        }
    }

    fun addRecievedListener(listener: MessageReceivedListener, tag: String) {
        listeners.remove(tag)
        listeners.put(tag, listener)
    }

    fun addRecievedListenerAndSubscribe(listener: MessageReceivedListener, tag: String, topic: String, qos: Int) {
        addRecievedListener(listener, tag)
        client?.subscribe(topic, qos)
    }

    fun removeRecievedListener(tag: String) {
        listeners.remove(tag)
    }

}

interface MessageReceivedListener {
    fun messageReceived(topic: String?, message: MqttMessage?)
}
