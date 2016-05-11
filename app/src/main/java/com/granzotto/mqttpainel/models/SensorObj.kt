package com.granzotto.mqttpainel.models

class SensorObj constructor(var topic: String?, var name: String?) {
    var value: String? = null

    override fun toString(): String {
        return "${name} (${topic}) = ${value}"
    }
}