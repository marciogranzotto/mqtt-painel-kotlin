package com.granzotto.mqttpainel.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.granzotto.mqttpainel.R
import com.granzotto.mqttpainel.models.EquipmentObj
import com.granzotto.mqttpainel.utils.extensions.inflate
import io.realm.RealmResults
import kotlinx.android.synthetic.main.equipment_cell.view.*

class EquipmentCardAdapter(var items: RealmResults<EquipmentObj>, val listener: EquipmentStateListener) : RecyclerView.Adapter<EquipmentCardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): EquipmentCardViewHolder? {
        val v = inflate(R.layout.equipment_cell, parent)
        return EquipmentCardViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: EquipmentCardViewHolder?, position: Int) {
        holder?.bindViewHolder(items[position], listener)
    }

}

class EquipmentCardViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun bindViewHolder(equipmentObj: EquipmentObj, listener: EquipmentStateListener) {
        itemView.title.text = equipmentObj.name
        itemView.subTitle.text = equipmentObj.topic
        itemView.stateSwitch.isEnabled = equipmentObj.getValueAsBoolean()

        itemView.stateSwitch.setOnCheckedChangeListener(
                CompoundButton.OnCheckedChangeListener { button, b ->
                    listener.stateChanged(equipmentObj, b)
                }
        )
    }

}

interface EquipmentStateListener {
    fun stateChanged(equipment: EquipmentObj, state: Boolean)
}