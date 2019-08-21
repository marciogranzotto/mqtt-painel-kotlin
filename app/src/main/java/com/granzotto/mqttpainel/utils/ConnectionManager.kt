package com.granzotto.mqttpainel.utils

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.granzotto.mqttpainel.BuildConfig
import com.granzotto.mqttpainel.utils.extensions.nullIfBlank
import com.pawegio.kandroid.d
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.util.*

object ConnectionManager {

    private var serverUrl: String? = ""
    private var serverPort: String? = ""
    private var userName: String? = ""
    private var userPassword: String? = ""

    var client: MqttAndroidClient? = null
    var connectionListener: ConnectionListener? = null
    var listeners: HashMap<String, MessageReceivedListener> = HashMap()

    fun setUp(serverUrl: String?, serverPort: String?, userName: String?, userPassword: String?) {
        this.serverUrl = serverUrl
        this.serverPort = serverPort
        this.userName = userName
        this.userPassword = userPassword
    }

    fun connect(appContext: Context) {
        var serverUrl = this.serverUrl.nullIfBlank() ?: return
        serverUrl = if (serverUrl.contains("//")) serverUrl else "tcp://$serverUrl"
        val serverPort = this.serverPort.nullIfBlank() ?: return

        val url = "$serverUrl:$serverPort"
        val clientID = "${Settings.Secure.getString(appContext.contentResolver, Settings.Secure.ANDROID_ID)}-android"

        if (BuildConfig.DEBUG)
            d("clientID: $clientID")

        client = MqttAndroidClient(appContext, url, clientID)
        client?.registerResources(appContext)
        val options = MqttConnectOptions()
        options.userName = userName
        options.password = userPassword?.toCharArray()
        client?.connect(options, null, object: IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                connectionListener?.onConnected()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                connectionListener?.onConnectionError(exception)
            }

        })

        client?.setCallback(object: MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                listeners.forEach {
                    it.value.messageReceived(topic, message)
                }
            }

            override fun connectionLost(cause: Throwable?) {
                if (BuildConfig.DEBUG)
                    Log.e("ConnectionManager", "Connection lost!", cause)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                if (BuildConfig.DEBUG)
                    d("deliveryComplete: \n$token")
            }

        })
    }

    fun disconnect() {
        client?.disconnect()
    }

    fun addReceivedListener(listener: MessageReceivedListener, tag: String) {
        listeners.remove(tag)
        listeners[tag] = listener
    }

    fun addReceivedListenerAndSubscribe(listener: MessageReceivedListener, tag: String, topic: String, qos: Int) {
        addReceivedListener(listener, tag)
        client?.subscribe(topic, qos)
    }

    fun removeReceivedListener(tag: String) {
        listeners.remove(tag)
    }

}

interface MessageReceivedListener {
    fun messageReceived(topic: String?, message: MqttMessage?)
}

interface ConnectionListener {
    fun onConnected()
    fun onConnectionError(exception: Throwable?)
    fun onConnectionLost()
}
