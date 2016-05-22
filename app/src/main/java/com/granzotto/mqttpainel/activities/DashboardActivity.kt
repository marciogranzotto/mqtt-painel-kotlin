package com.granzotto.mqttpainel.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.granzotto.mqttpainel.R
import com.granzotto.mqttpainel.fragments.EquipmentsFragment
import com.granzotto.mqttpainel.fragments.SensorsFragment
import com.granzotto.mqttpainel.utils.ConnectionManager
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        inflateCards();
    }

    override fun onRestart() {
        super.onRestart()
        if (!(ConnectionManager.client?.isConnected ?: false)) {
            //Disconnected!!
            ConnectionManager.connect(applicationContext)
        }
    }

    private fun inflateCards() {
        val rootViewID = contentView.id;
        contentView.removeAllViews()

        fragmentManager.beginTransaction()
                .add(rootViewID, SensorsFragment(), SensorsFragment.TAG)
                .add(rootViewID, EquipmentsFragment(), EquipmentsFragment.TAG)
                .commit()
    }
}
