package com.granzotto.mqttpainel

import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.android.service.MqttAndroidClient

class MainActivity : AppCompatActivity() {

    companion object {
        val SERVER_URL = "casa-granzotto.ddns.net:1883"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val client = MqttAndroidClient(this, MainActivity.SERVER_URL, Settings.Secure.ANDROID_ID);

        textView.text = "Testing"
    }
}
