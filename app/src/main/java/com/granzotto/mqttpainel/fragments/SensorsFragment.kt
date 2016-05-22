package com.granzotto.mqttpainel.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.granzotto.mqttpainel.R
import com.granzotto.mqttpainel.activities.AddEditSensorActivity
import com.granzotto.mqttpainel.adapters.SensorCardAdapter
import com.granzotto.mqttpainel.adapters.SensorListener
import com.granzotto.mqttpainel.models.SensorObj
import com.granzotto.mqttpainel.presenters.SensorsCardPresenter
import com.granzotto.mqttpainel.utils.ConnectionManager
import com.granzotto.mqttpainel.utils.MessageReceivedListener
import com.granzotto.mqttpainel.utils.ObjectParcer
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_sensors.*
import nucleus.factory.RequiresPresenter
import nucleus.view.NucleusFragment
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.jetbrains.anko.startActivity

@RequiresPresenter(SensorsCardPresenter::class)
class SensorsFragment : NucleusFragment<SensorsCardPresenter>(), MessageReceivedListener, SensorListener {

    companion object {
        val TAG = "SensorsFragment"
    }

    var adapter: SensorCardAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_sensors, container, false)
    }

    override fun onStart() {
        super.onStart()
        addButton.setOnClickListener { addButtonClicked() }
        ConnectionManager.addRecievedListener(this, TAG)
        presenter.requestSensors()
    }

    private fun addButtonClicked() {
        startActivity<AddEditSensorActivity>()
    }

    override fun messageReceived(topic: String?, message: MqttMessage?) {
        if (topic != null) {
            presenter.messageReceived(topic, message)
        }
    }

    override fun onStop() {
        ConnectionManager.removeRecievedListener(SensorsFragment.TAG)
        super.onStop()
    }


    fun onSensorsSuccess(results: RealmResults<SensorObj>) {
        adapter = SensorCardAdapter(results, this)
        recyclerView.adapter = adapter

        for (i in 0..results.lastIndex) {
            val it = results[i]
            ConnectionManager.client?.subscribe(it.topic, 0)
        }
    }

    fun reloadSensors(results: RealmResults<SensorObj>) {
        adapter?.items = results
        adapter?.notifyDataSetChanged()
    }

    override fun onSensorClicked(sensor: SensorObj) {
        ObjectParcer.putObject(AddEditSensorActivity.SENSOR, sensor)
        startActivity<AddEditSensorActivity>()
    }
}

