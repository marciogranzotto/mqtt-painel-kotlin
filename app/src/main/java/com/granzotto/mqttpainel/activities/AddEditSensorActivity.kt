package com.granzotto.mqttpainel.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.granzotto.mqttpainel.R
import com.granzotto.mqttpainel.models.SensorObj
import com.granzotto.mqttpainel.utils.ConnectionManager
import com.granzotto.mqttpainel.utils.ObjectParcer
import com.pawegio.kandroid.e
import com.pawegio.kandroid.textWatcher
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_add_sensor.*

class AddEditSensorActivity : AppCompatActivity() {

    companion object {
        val SENSOR = "edit_sensor"
    }


    private var topic: String? = null
    private var name: String? = null
    private var sensor: SensorObj? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sensor)
        setUpTextWatchers()

        sensor = ObjectParcer.getObject(SENSOR) as SensorObj?

        if (sensor == null) {
            addButton.setOnClickListener { addSensor() }
            addButton.visibility = View.VISIBLE
            saveButton.visibility = View.GONE
            deleteButton.visibility = View.GONE
        } else {
            saveButton.setOnClickListener { editSensor() }
            deleteButton.setOnClickListener { deleteSensor() }
            addButton.visibility = View.GONE
            saveButton.visibility = View.VISIBLE
            deleteButton.visibility = View.VISIBLE

            etName.setText(sensor?.name)
            etTopic.setText(sensor?.topic)
        }
    }

    private fun deleteSensor() {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        sensor?.deleteFromRealm()
        realm.commitTransaction()
        finish()
    }

    private fun editSensor() {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        if (topic != null && topic != sensor?.topic) {
            sensor?.topic = topic!!
            sensor?.value = null
            ConnectionManager.client?.subscribe(topic, 0) ?: e("Error subscribing to topic")
        }
        if (name != null) sensor?.name = name!!
        realm.commitTransaction()
        finish()
    }

    private fun addSensor() {
        ConnectionManager.client?.subscribe(topic, 0) ?: e("Error subscribing to topic")
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val sensor = realm.createObject(SensorObj::class.java, topic!!)
        sensor.name = name!!
        realm.commitTransaction()
        finish()
    }

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
