package com.dsige.reparto.dominion.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.data.local.model.Photo
import com.dsige.reparto.dominion.data.viewModel.RepartoViewModel
import com.dsige.reparto.dominion.data.viewModel.ViewModelFactory
import com.dsige.reparto.dominion.helper.Gps
import com.dsige.reparto.dominion.helper.Util
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_preview_camera.*
import java.io.File
import java.lang.Exception
import javax.inject.Inject

class PreviewCameraActivity : DaggerAppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabOk ->
                saveFotoReparto()

            R.id.fabClose -> if (tipo == 1) {
                startActivity(
                    Intent(this, CameraActivity::class.java)
                        .putExtra("repartoId", repartoId)
                        .putExtra("tipo", tipo)
                )
                finish()
            } else finish()
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var repartoViewModel: RepartoViewModel

    private var repartoId = 0
    private var tipo = 0
    private var nameImg = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_camera)
        Log.i("TAG","INGRESO A PREVIEW")
        val b = intent.extras
        if (b != null) {
            repartoId = b.getInt("repartoId")
            nameImg = b.getString("nameImg")!!
            tipo = b.getInt("tipo")
            bindUI()
        }
    }

    private fun bindUI() {
        repartoViewModel =
            ViewModelProvider(this, viewModelFactory).get(RepartoViewModel::class.java)

        fabClose.setOnClickListener(this)
        fabOk.setOnClickListener(this)
        textViewImg.text = nameImg

        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                val f = File(Util.getFolder(this), nameImg)
                Picasso.get().load(f)
                    .into(imageView, object : Callback {
                        override fun onSuccess() {
                            progressBar.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {

                        }
                    })
            }, 700)
        }

        repartoViewModel.mensajeError.observe(this) {
            Util.toastMensaje(this, it)
        }

        repartoViewModel.mensajeSuccess.observe(this) {
            if (it != null) {
                finish()
            }
        }

        if (tipo == 0) {
            fabOk.visibility = View.GONE
        }
    }

    private fun saveFotoReparto() {
        val gps = Gps(this@PreviewCameraActivity)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() == "0.0" || gps.longitude.toString() == "0.0") {
                gps.showAlert(this@PreviewCameraActivity)
            } else {
                val photo = Photo(
                    0,
                    repartoId,
                    nameImg,
                    Util.getFechaActual(),
                    5,
                    1,
                    gps.latitude.toString(),
                    gps.longitude.toString()
                )
                repartoViewModel.savePhotoReparto(photo)
            }
        } else {
            gps.showSettingsAlert(this@PreviewCameraActivity)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (tipo == 1) {
                startActivity(
                    Intent(this, CameraActivity::class.java)
                        .putExtra("repartoId", repartoId)
                        .putExtra("tipo", tipo)
                )
                finish()
            } else finish()
        }
        return super.onKeyDown(keyCode, event)
    }
}