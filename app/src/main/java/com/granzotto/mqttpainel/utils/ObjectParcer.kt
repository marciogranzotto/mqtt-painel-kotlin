package com.granzotto.mqttpainel.utils

import java.util.*

/**
 * Created by marciogranzotto on 5/22/16.
 */
object ObjectParcer {

    private val map = LinkedHashMap<String, Any>()

    fun putObject(key: String, obj: Any) {
        map.put(key, obj)
    }

    fun getObject(key: String): Any? {
        val obj = map.get(key)
        map.remove(key)
        return obj
    }
}