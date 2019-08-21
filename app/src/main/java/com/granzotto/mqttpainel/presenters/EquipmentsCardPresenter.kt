package com.granzotto.mqttpainel.presenters

import com.granzotto.mqttpainel.BuildConfig
import com.granzotto.mqttpainel.fragments.EquipmentsFragment
import com.granzotto.mqttpainel.models.EquipmentObj
import com.granzotto.mqttpainel.utils.ConnectionManager
import com.pawegio.kandroid.d
import com.pawegio.kandroid.i
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import org.eclipse.paho.client.mqttv3.MqttMessage

/**
 * Created by marciogranzotto on 5/17/16.
 */
class EquipmentsCardPresenter constructor(var view: EquipmentsFragment?) {

    private var realm = Realm.getDefaultInstance()
    private var compositeDisposable = CompositeDisposable()

    fun onDestroy() {
        view = null
        compositeDisposable.clear()
    }

    fun requestEquipments() {
        realm?.where(EquipmentObj::class.java)?.findAllAsync()?.asFlowable()
                ?.distinctUntilChanged()
                ?.subscribe({
                    view?.onEquipmentsSuccess(it)
                }, {
                    it.printStackTrace()
                })?.let { compositeDisposable.add(it) }
    }

    fun messageReceived(topic: String, message: MqttMessage?) {
        if (BuildConfig.DEBUG) i("Received: $topic = ${message.toString()}")
        realm?.where(EquipmentObj::class.java)?.equalTo("topic", topic)?.findAll()
                ?.asFlowable()
                ?.distinctUntilChanged()
                ?.subscribe({ results ->
                    realm?.beginTransaction()
                    results.forEach { equip ->
                        equip?.value = message.toString()
                        i(equip.toString())
                    }
                    realm?.commitTransaction()
                    view?.reloadEquipments()
                }, {
                    it.printStackTrace()
                })?.let { compositeDisposable.add(it) }
    }

    fun stateChanged(equipment: EquipmentObj, state: Boolean) {
        if (BuildConfig.DEBUG) d { "stateChanged on ${equipment.name}(${equipment.topic}) is now $state" }
        val message = if (state) MqttMessage("on".toByteArray()) else MqttMessage("off".toByteArray())
        message.qos = 1
        ConnectionManager.client?.publish(equipment.topic, message)
    }

}