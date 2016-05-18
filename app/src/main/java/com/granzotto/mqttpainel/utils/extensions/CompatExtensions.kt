package com.granzotto.mqttpainel.utils.extensions

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun <T:RecyclerView.ViewHolder> RecyclerView.Adapter<T>.inflate(layoutId: Int, parent: ViewGroup?): View {
    return LayoutInflater.from(parent?.context).inflate(layoutId, parent, false)
}