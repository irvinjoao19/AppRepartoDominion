package com.dsige.reparto.dominion.ui.activities

import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.data.viewModel.RepartoViewModel
import com.dsige.reparto.dominion.data.viewModel.ViewModelFactory
import com.dsige.reparto.dominion.helper.Util
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_firm.*
import javax.inject.Inject

class FirmActivity : DaggerAppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabFirma -> {
                if (paintView.validDraw()) {
                    val name = paintView.save(this, repartoId, 7, "")
                    repartoViewModel.getUpdateRegistro(repartoId, name)
                } else {
                    repartoViewModel.setError("Debes de Firmar.")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.firma, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {
                paintView.clear()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var repartoViewModel: RepartoViewModel
    private var repartoId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firm)
        val b = intent.extras
        if (b != null) {
            repartoId = b.getInt("repartoId")
            bindUI()
        }
    }

    private fun bindUI() {
        repartoViewModel =
            ViewModelProvider(this, viewModelFactory).get(RepartoViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Firma"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
            paintView.initNew(windowMetrics)
        } else {
            val metrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(metrics)
            paintView.init(metrics)
        }
        fabFirma.setOnClickListener(this)

        repartoViewModel.mensajeError.observe(this, {
            Util.toastMensaje(this, it)
        })
        repartoViewModel.mensajeSuccess.observe(this, {
            if (it != null) {
                Util.toastMensaje(this, it)
                finish()
            }
        })
    }
}