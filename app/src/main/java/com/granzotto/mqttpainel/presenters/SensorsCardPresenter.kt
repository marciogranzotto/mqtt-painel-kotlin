package com.granzotto.mqttpainel.presenters

import android.os.Bundle
import com.granzotto.mqttpainel.fragments.SensorsFragment
import com.granzotto.mqttpainel.models.SensorObj
import com.granzotto.mqttpainel.utils.ConnectionManager
import com.pawegio.kandroid.e
import com.pawegio.kandroid.i
import io.realm.Realm
import io.realm.RealmResults
import nucleus.presenter.RxPresenter
import org.eclipse.paho.client.mqttv3.MqttMessage
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.*

class SensorsCardPresenter : RxPresenter<SensorsFragment>() {

    companion object {
        val SENSORS_REQUEST = 0
    }

    private var realm: Realm? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        realm = Realm.getDefaultInstance()

        restartableLatestCache(SENSORS_REQUEST,
                { queryForSensors()?.observeOn(AndroidSchedulers.mainThread()) },
                { view, response -> view.onSensorsSuccess(response) },
                { view, throwable -> e("Errow!\n${throwable.cause}") })
    }

    fun queryForSensors(): Observable<RealmResults<SensorObj>>? {
        return realm?.where(SensorObj::class.java)?.findAll()?.asObservable()?.distinctUntilChanged()
    }

    fun requestSensors() {
        start(SENSORS_REQUEST)
    }

    fun addAndSubscribeTopics(topics: LinkedList<SensorObj>) {
        realm?.beginTransaction()
        topics.forEach { ConnectionManager.client?.subscribe(it.topic, 0) }
        realm?.copyToRealmOrUpdate(topics)
        realm?.commitTransaction()
    }

    fun messageReceived(topic: String?, message: MqttMessage?) {
        var str: String = "Received:"
        i("Received: ${topic} = ${message.toString()}")
        val results = realm?.where(SensorObj::class.java)?.equalTo(SensorObj.TOPIC, topic)?.findAll()
        realm?.beginTransaction()
        results?.forEach { it.value = message.toString() }
        realm?.commitTransaction()
    }

}