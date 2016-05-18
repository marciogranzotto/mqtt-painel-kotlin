package com.granzotto.mqttpainel.presenters

import android.os.Bundle
import com.granzotto.mqttpainel.BuildConfig
import com.granzotto.mqttpainel.fragments.EquipmentsFragment
import com.granzotto.mqttpainel.models.EquipmentObj
import com.granzotto.mqttpainel.utils.ConnectionManager
import com.pawegio.kandroid.d
import com.pawegio.kandroid.e
import com.pawegio.kandroid.i
import io.realm.Realm
import io.realm.RealmResults
import nucleus.presenter.RxPresenter
import org.eclipse.paho.client.mqttv3.MqttMessage
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by marciogranzotto on 5/17/16.
 */
class EquipmentsCardPresenter : RxPresenter<EquipmentsFragment>() {

    companion object {
        val EQUIPMENTS_REQUEST = 0
        val MESSAGE_RECIEVED = 1
        val RELOAD_EQUIPMENTS = 2
    }

    private var realm: Realm? = null
    private var topic: String? = null
    private var message: MqttMessage? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        realm = Realm.getDefaultInstance()

        restartableLatestCache(EQUIPMENTS_REQUEST,
                { queryForEquipments()?.observeOn(AndroidSchedulers.mainThread()) },
                { view, response -> view.onEquipmentsSuccess(response) },
                { view, throwable -> e("Errow!\n${throwable.cause}") })

        restartableLatestCache(RELOAD_EQUIPMENTS,
                { queryForEquipments()?.observeOn(AndroidSchedulers.mainThread()) },
                { view, response -> view.reloadEquipments(response) },
                { view, throwable -> e("Errow!\n${throwable.cause}") })

        restartableLatestCache(MESSAGE_RECIEVED,
                { queryForSpecificEquipments(topic)?.observeOn(AndroidSchedulers.mainThread()) },
                { view, response ->
                    realm?.beginTransaction()
                    for (i in 0..response.lastIndex) {
                        val it = response[i]
                        it.value = message.toString()
                        i(it.toString())
                    }
                    realm?.commitTransaction()
                    start(RELOAD_EQUIPMENTS)
                },
                { view, t ->
                    e("Something went wrong on realm!")
                    t.printStackTrace()
                }
        )
    }

    private fun queryForEquipments(): Observable<RealmResults<EquipmentObj>>? {
        return realm?.where(EquipmentObj::class.java)?.findAll()?.asObservable()?.distinctUntilChanged()
    }

    private fun queryForSpecificEquipments(topic: String?): Observable<RealmResults<EquipmentObj>>? {
        return realm?.where(EquipmentObj::class.java)?.equalTo("topic", topic)?.findAll()?.asObservable()?.distinctUntilChanged()
    }

    fun requestEquipments() {
        start(EQUIPMENTS_REQUEST)
    }

    fun messageRecieved(topic: String, message: MqttMessage?) {
        if (BuildConfig.DEBUG) i("Received: ${topic} = ${message.toString()}")
        this.topic = topic
        this.message = message
        start(MESSAGE_RECIEVED)
    }

    fun stateChanged(equipment: EquipmentObj, state: Boolean) {
        if (BuildConfig.DEBUG) d { "stateChanged on ${equipment.name}(${equipment.topic}) is now $state" }
        val message = if (state) MqttMessage("on".toByteArray()) else MqttMessage("off".toByteArray())
        message.qos = 1
        ConnectionManager.client?.publish(equipment.topic, message)
    }

}