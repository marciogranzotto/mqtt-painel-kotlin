package com.granzotto.mqttpainel

import android.app.NotificationManager
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MainActivity : AppCompatActivity() {

    companion object {
        val SERVER_URL = "tcp://casa-granzotto.ddns.net:1883"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val client = MqttAndroidClient(this, MainActivity.SERVER_URL, Settings.Secure.ANDROID_ID);
        client.registerResources(this)
        val options = MqttConnectOptions();
        options.userName = "osmc"
        options.password = "84634959".toCharArray()
        client.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                textView.text = topic + " = " + message.toString()
            }

            override fun connectionLost(cause: Throwable?) {
                textView.text = "Connection Lost!"
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                textView.text = "Delivery Complete!"
            }

        })
        client.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                textView.text = "Connected"
                client.subscribe("/home/bedroom/led", 0)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                textView.text = "Error connecting"
                exception?.printStackTrace()
            }

        })
    }
}
