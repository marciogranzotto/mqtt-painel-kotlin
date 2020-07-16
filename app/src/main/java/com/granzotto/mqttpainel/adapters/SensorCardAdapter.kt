package com.granzotto.mqttpainel.adapters

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import com.granzotto.mqttpainel.R
import com.granzotto.mqttpainel.models.SensorObj
import com.granzotto.mqttpainel.utils.extensions.inflate
import io.realm.RealmResults
import kotlinx.android.synthetic.main.sensor_cell.view.*

/**
 * Created by marciogranzotto on 5/12/16.
 */

class SensorCardAdapter(var items: RealmResults<SensorObj>, private val listener: SensorListener): RecyclerView.Adapter<SensorCardViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SensorCardViewHolder, position: Int) {
        items[position]?.let { holder.bindViewHolder(it, listener) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorCardViewHolder {
        val v = inflate(R.layout.sensor_cell, parent)
        return SensorCardViewHolder(v)
    }

}

class SensorCardViewHolder(val view: View): RecyclerView.ViewHolder(view) {

    private val accentColor = ContextCompat.getColor(view.context, R.color.colorAccent);

    fun bindViewHolder(sensor: SensorObj, listener: SensorListener) {
        itemView.title.text = sensor.name
        itemView.subTitle.text = sensor.topic
        itemView.value.text = sensor.value
        val textColor = itemView.title.currentTextColor
        val anim = ValueAnimator.ofObject(ArgbEvaluator(), accentColor, textColor)
        anim.duration = 3000
        anim.interpolator = DecelerateInterpolator()
        anim.addUpdateListener { itemView.value.setTextColor(it.animatedValue as Int) }
        anim.start()
        itemView.setOnClickListener { listener.onSensorClicked(sensor) }
    }
}

interface SensorListener {
    fun onSensorClicked(sensor: SensorObj)
}