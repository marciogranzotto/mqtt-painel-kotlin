package com.granzotto.mqttpainel.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by marciogranzotto on 5/17/16.
 */
open class EquipmentObj constructor(@PrimaryKey open var topic: String = "", open var name: String = "", open var value: String? = null) : RealmObject() {
    override fun toString(): String {
        return "${name} (${topic}) = ${value}"
    }

    companion object {
        val TOPIC: String = "topic"
    }

    fun getValueAsBoolean(): Boolean {
        return value.equals("on", true) || value.equals("true", true)
    }
}