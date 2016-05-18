package com.granzotto.mqttpainel.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.granzotto.mqttpainel.R
import com.granzotto.mqttpainel.models.SensorObj
import com.granzotto.mqttpainel.utils.ConnectionManager
import com.pawegio.kandroid.e
import com.pawegio.kandroid.textWatcher
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_add_sensor.*

class AddSensorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sensor)

        setUpTextWatchers()

        addButton.setOnClickListener { v ->
            addButtonClicked();
        }
    }

    private fun addButtonClicked() {
        ConnectionManager.client?.subscribe(topic, 0) ?: e("Error subscribing to topic")
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val sensor = realm.createObject(SensorObj::class.java)
        sensor.name = name!!
        sensor.topic = topic!!
        realm.commitTransaction()
        finish()
    }

    private var topic: String? = null
    private var name: String? = null

    private fun setUpTextWatchers() {
        etTopic.textWatcher {
            afterTextChanged { text ->
                topic = text.toString().trim()
                shouldEnableButton()
            }
        }
        etName.textWatcher {
            afterTextChanged { text ->
                name = text.toString().trim()
                shouldEnableButton()
            }
        }
    }

    private fun shouldEnableButton() {
        addButton.isEnabled = !topic.isNullOrEmpty() && !name.isNullOrEmpty()
    }
}
