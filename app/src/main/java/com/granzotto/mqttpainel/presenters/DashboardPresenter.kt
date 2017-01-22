package com.granzotto.mqttpainel.presenters

import android.support.v7.app.AppCompatActivity
import com.granzotto.mqttpainel.activities.DashboardActivity
import com.granzotto.mqttpainel.fragments.EquipmentsFragment
import com.granzotto.mqttpainel.fragments.SensorsFragment
import com.granzotto.mqttpainel.models.ConnectionObj
import com.granzotto.mqttpainel.utils.ConnectionManager
import com.granzotto.mqttpainel.utils.MyConstants
import kotlinx.android.synthetic.main.activity_dashboard.*

/**
 * Created by marciogranzotto on 21/01/17.
 */

class DashboardPresenter(var view: DashboardActivity?) {

    fun onCreate() {
        val rootViewID = view?.contentView?.id ?: return
        view?.contentView?.removeAllViews()

        view?.fragmentManager?.beginTransaction()
                ?.add(rootViewID, SensorsFragment(), SensorsFragment.TAG)
                ?.add(rootViewID, EquipmentsFragment(), EquipmentsFragment.TAG)
                ?.commit()
    }

    fun onRestart() {
        if (!(ConnectionManager.client?.isConnected ?: false)) {
            //Disconnected!!
            if (view == null) return
            ConnectionManager.connect(view!!.applicationContext)
        }
    }

    fun onDestroy() {
        view = null
    }

    fun onDisconnectOptionItemSelected() {
        view?.showProgressDialog()
        ConnectionManager.disconnect()
        val connectionObj = readSharedPreferences()
        clearSharedPreferences()
        view?.dismissProgressDialog()
        view?.goToMainActivity(connectionObj)
    }

    private fun clearSharedPreferences() {
        val sharedPreferences = view?.getSharedPreferences(MyConstants.SHARED_PREFERENCES, AppCompatActivity.MODE_PRIVATE)
        sharedPreferences?.edit()?.clear()?.apply()
    }

    private fun readSharedPreferences(): ConnectionObj? {
        val prefs = view?.getSharedPreferences(MyConstants.SHARED_PREFERENCES, AppCompatActivity.MODE_PRIVATE)
        val serverUrl = prefs?.getString(MyConstants.SERVER_URL, "")
        val serverPort = prefs?.getString(MyConstants.SERVER_PORT, "1883")
        val user = prefs?.getString(MyConstants.SERVER_USER, null)
        val password = prefs?.getString(MyConstants.SERVER_USER_PASSWORD, null)
        if (serverPort == null || serverUrl == null) return null
        return ConnectionObj(serverUrl, serverPort, user, password)
    }

}