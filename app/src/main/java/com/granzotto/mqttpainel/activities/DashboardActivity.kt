package com.granzotto.mqttpainel.activities

import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import com.granzotto.mqttpainel.R
import com.granzotto.mqttpainel.presenters.DashboardPresenter
import org.jetbrains.anko.startActivity

class DashboardActivity : BaseActivity() {

    var presenter: DashboardPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        presenter = DashboardPresenter(this)
        presenter?.onCreate()
    }

    override fun onDestroy() {
        presenter?.onDestroy()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_disconnect) {
            presenter?.onDisconnectOptionItemSelected()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRestart() {
        super.onRestart()
        presenter?.onRestart()
    }

    fun goToMainActivity(extra: Parcelable?) {
        if (extra != null)
            startActivity<MainActivity>(MainActivity.CONNECTION_OBJ to extra)
        else
            startActivity<MainActivity>()
        finish()
    }
}
