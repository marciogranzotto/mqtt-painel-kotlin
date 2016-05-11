package com.granzotto.mqttpainel.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.granzotto.mqttpainel.R
import com.granzotto.mqttpainel.models.SensorObj
import com.granzotto.mqttpainel.utils.ConnectionManager
import com.granzotto.mqttpainel.utils.MessageReceivedListener
import com.pawegio.kandroid.toast
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class SensorsFragment : Fragment(), MessageReceivedListener {

    companion object {
        val TAG = "SensorsFragment"
    }

    var topics = LinkedList<SensorObj>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_sensors, container, false)
    }

    override fun onStart() {
        super.onStart()
        //TODO remove this:
        topics.add(SensorObj("/home/bedroom/led", "Bedroom LED"))
        topics.forEach { ConnectionManager.client?.subscribe(it.topic, 0) }

        ConnectionManager.addRecievedListener(this, TAG)
    }

    override fun messageReceived(topic: String?, message: MqttMessage?) {
        var str: String = "Received:"
        topics
                .filter { it.topic.equals(topic) }
                .sortedBy { it.name }
                .forEach {
                    it.value = message?.toString()
                    str += "\n${it.toString()}"
                }
        toast(str)
    }

}