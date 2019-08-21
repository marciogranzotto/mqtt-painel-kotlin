package com.granzotto.mqttpainel.presenters

import com.granzotto.mqttpainel.fragments.SensorsFragment
import com.granzotto.mqttpainel.models.SensorObj
import com.pawegio.kandroid.i
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import org.eclipse.paho.client.mqttv3.MqttMessage

/**
 * Created by marciogranzotto on 09/01/17.
 */

class SensorsCardPresenter constructor(var view: SensorsFragment?) {

    private var realm = Realm.getDefaultInstance()
    private var compositeDisposable = CompositeDisposable()

    fun onDestroy() {
        view = null
    }

    fun requestSensors() {
        realm?.where(SensorObj::class.java)?.findAll()
                ?.asFlowable()
                ?.distinctUntilChanged()
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    view?.onSensorsSuccess(it)
                }, {
                    it.printStackTrace()
                })?.let { compositeDisposable.add(it) }
    }

    fun messageReceived(topic: String, message: MqttMessage?) {
        i { "messageReceived called" }
        if (message == null) return
        realm?.where(SensorObj::class.java)?.equalTo("topic", topic)?.findFirst()
                ?.asFlowable<SensorObj>()
                ?.distinctUntilChanged()
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    realm?.beginTransaction()
                    it.value = message.toString()
                    realm?.commitTransaction()
                    i { it.toString() }
                    view?.reloadSensor(it)
                }, {
                    it.printStackTrace()
                })?.let { compositeDisposable.add(it) }
    }

}
