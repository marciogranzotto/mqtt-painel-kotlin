package com.granzotto.mqttpainel.utils.extensions

fun String?.nullIfBlank() = if (this.isNullOrBlank()) null else this

fun String?.nullIfEmpty() = if (this.isNullOrEmpty()) null else this