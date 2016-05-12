package com.granzotto.mqttpainel.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SensorObj constructor(@PrimaryKey open var topic: String = "", open var name: String = "", open var value: String? = null) : RealmObject() {
    override fun toString(): String {
        return "${name} (${topic}) = ${value}"
    }

    companion object {
        val TOPIC = "topic"
    }
}