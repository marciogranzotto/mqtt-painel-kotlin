package com.granzotto.mqttpainel.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.granzotto.mqttpainel.R
import com.granzotto.mqttpainel.activities.AddEditEquipmentActivity
import com.granzotto.mqttpainel.adapters.EquipmentCardAdapter
import com.granzotto.mqttpainel.adapters.EquipmentListener
import com.granzotto.mqttpainel.models.EquipmentObj
import com.granzotto.mqttpainel.presenters.EquipmentsCardPresenter
import com.granzotto.mqttpainel.utils.ConnectionManager
import com.granzotto.mqttpainel.utils.MessageReceivedListener
import com.granzotto.mqttpainel.utils.ObjectParcer
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_equipments.*
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.jetbrains.anko.startActivity

/**import org.jetbrains.anko.startActivity

 * Created by marciogranzotto on 5/17/16.
 */

class EquipmentsFragment : Fragment(), MessageReceivedListener, EquipmentListener {

    companion object {
        val TAG = "EquipmentsFragment"
    }

    var adapter: EquipmentCardAdapter? = null
    var presenter = EquipmentsCardPresenter(this)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_equipments, container, false)
    }

    override fun onStart() {
        super.onStart()
        addButton.setOnClickListener { addButtonClicked() }
        ConnectionManager.addRecievedListener(this, TAG)
        presenter.requestEquipments()
    }

    override fun onStop() {
        ConnectionManager.removeRecievedListener(TAG)
        super.onStop()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    private fun addButtonClicked() {
        startActivity<AddEditEquipmentActivity>()
    }

    override fun messageReceived(topic: String?, message: MqttMessage?) {
        if (topic != null) {
            presenter.messageRecieved(topic, message)
        }
    }

    override fun onEquipmentClicked(equipment: EquipmentObj) {
        ObjectParcer.putObject(AddEditEquipmentActivity.EQUIPMENT, equipment)
        startActivity<AddEditEquipmentActivity>()
    }

    fun reloadEquipments() {
        adapter?.notifyDataSetChanged()
    }

    fun onEquipmentsSuccess(response: RealmResults<EquipmentObj>) {
        adapter = EquipmentCardAdapter(response, this)
        recyclerView.adapter = adapter

        response.forEach {
            ConnectionManager.client?.subscribe(it.topic, 0)
        }
    }

    override fun onStateChanged(equipment: EquipmentObj, state: Boolean) {
        presenter.stateChanged(equipment, state)
    }
}

