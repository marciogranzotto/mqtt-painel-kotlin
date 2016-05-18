package com.granzotto.mqttpainel.presenters

import android.os.Bundle
import com.granzotto.mqttpainel.BuildConfig
import com.granzotto.mqttpainel.fragments.SensorsFragment
import com.granzotto.mqttpainel.models.SensorObj
import com.pawegio.kandroid.e
import com.pawegio.kandroid.i
import io.realm.Realm
import io.realm.RealmResults
import nucleus.presenter.RxPresenter
import org.eclipse.paho.client.mqttv3.MqttMessage
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

class SensorsCardPresenter : RxPresenter<SensorsFragment>() {

    companion object {
        val SENSORS_REQUEST = 0
        val MESSAGE_RECIEVED = 1
        val RELOAD_SENSORS = 2
    }

    private var realm: Realm? = null
    private var topic: String? = null
    private var message: MqttMessage? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        realm = Realm.getDefaultInstance()

        restartableLatestCache(SENSORS_REQUEST,
                { queryForSensors()?.observeOn(AndroidSchedulers.mainThread()) },
                { view, response -> view.onSensorsSuccess(response) },
                { view, throwable -> e("Errow!\n${throwable.cause}") })

        restartableLatestCache(RELOAD_SENSORS,
                { queryForSensors()?.observeOn(AndroidSchedulers.mainThread()) },
                { view, response -> view.reloadSensors(response) },
                { view, throwable -> e("Errow!\n${throwable.cause}") })

        restartableLatestCache(MESSAGE_RECIEVED,
                { queryForSpecificSensors(topic)?.observeOn(AndroidSchedulers.mainThread()) },
                { view, response ->
                    realm?.beginTransaction()
                    for (i in 0..response.lastIndex) {
                        val it = response[i]
                        it.value = message.toString()
                        i(it.toString())
                    }
                    realm?.commitTransaction()
                    start(RELOAD_SENSORS)
                },
                { view, t ->
                    e("Something went wrong on realm!")
                    t.printStackTrace()
                }
        )
    }

    private fun queryForSensors(): Observable<RealmResults<SensorObj>>? {
        return realm?.where(SensorObj::class.java)?.findAll()?.asObservable()?.distinctUntilChanged()
    }

    private fun queryForSpecificSensors(topic: String?): Observable<RealmResults<SensorObj>>? {
        return realm?.where(SensorObj::class.java)?.equalTo("topic", topic)?.findAll()?.asObservable()?.distinctUntilChanged()
    }

    fun requestSensors() {
        start(SENSORS_REQUEST)
    }

    fun messageReceived(topic: String, message: MqttMessage?) {
        if (BuildConfig.DEBUG) i("Received: ${topic} = ${message.toString()}")
        this.topic = topic
        this.message = message
        start(MESSAGE_RECIEVED)
    }

}