package com.granzotto.mqttpainel.presenters

import com.granzotto.mqttpainel.fragments.SensorsFragment
import com.granzotto.mqttpainel.models.SensorObj
import com.pawegio.kandroid.i
import io.realm.Realm
import org.eclipse.paho.client.mqttv3.MqttMessage
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.onError

/**
 * Created by marciogranzotto on 09/01/17.
 */

class SensorsCardPresenter constructor(var view: SensorsFragment?) {

    private var realm = Realm.getDefaultInstance()

    fun onDestroy() {
        view = null
    }

    fun requestSensors() {
        realm?.where(SensorObj::class.java)?.findAll()?.asObservable()?.
                distinctUntilChanged()?.
                observeOn(AndroidSchedulers.mainThread())?.
                doOnNext {
                    view?.onSensorsSuccess(it)
                }?.onError { it.printStackTrace() }?.subscribe()
    }

    fun messageReceived(topic: String, message: MqttMessage?) {
        i { "messageReceived called" }
        if (message == null) return
        realm?.where(SensorObj::class.java)?.equalTo("topic", topic)?.
                findFirst()?.
                asObservable<SensorObj>()?.
                distinctUntilChanged()?.
                observeOn(AndroidSchedulers.mainThread())?.
                doOnNext {
                    realm?.beginTransaction()
                    it.value = message.toString()
                    realm?.commitTransaction()
                    i { it.toString() }
                    view?.reloadSensor(it)
                }?.onError { it.printStackTrace() }?.subscribe()
    }

}
