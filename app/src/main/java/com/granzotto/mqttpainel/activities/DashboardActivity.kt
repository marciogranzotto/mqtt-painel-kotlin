package com.granzotto.mqttpainel.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.granzotto.mqttpainel.R
import com.granzotto.mqttpainel.fragments.SensorsFragment
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        inflateCards();
    }

    private fun inflateCards() {
        val rootViewID = contentView.id;
        supportFragmentManager.beginTransaction()
                .add(rootViewID, SensorsFragment(), SensorsFragment.TAG)
                .commit()
    }
}
