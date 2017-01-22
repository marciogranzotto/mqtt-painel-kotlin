package com.granzotto.mqttpainel.activities

import android.os.Bundle
import com.granzotto.mqttpainel.R
import com.granzotto.mqttpainel.models.ConnectionObj
import com.granzotto.mqttpainel.presenters.MainPresenter
import com.pawegio.kandroid.textWatcher
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : BaseActivity() {

    companion object {
        val CONNECTION_OBJ = "connection_object"
    }

    private var presenter: MainPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = MainPresenter(this)
        setUpTextWatchers()
        connectButton.setOnClickListener { v -> presenter?.onConnectButtonClicked() }
        presenter?.onIntentExtras(intent?.extras?.getParcelable<ConnectionObj>(CONNECTION_OBJ))
        presenter?.onCreate()
    }

    override fun onDestroy() {
        presenter?.onDestroy()
        super.onDestroy()
    }

    private fun setUpTextWatchers() {
        etServer.textWatcher {
            afterTextChanged { text ->
                presenter?.onServerTextChanged(text.toString())
            }
        }
        etPort.textWatcher {
            afterTextChanged { text ->
                presenter?.onPortTextChanged(text.toString())
            }
        }
    }

    fun goToDashboardScreen() {
        startActivity<DashboardActivity>()
        finish()
    }
}