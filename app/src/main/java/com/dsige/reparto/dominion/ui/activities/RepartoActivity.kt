package com.dsige.reparto.dominion.ui.activities

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsige.reparto.dominion.ui.adapters.PhotoAdapter
import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.data.local.model.Photo
import com.dsige.reparto.dominion.data.local.model.Registro
import com.dsige.reparto.dominion.data.local.model.Reparto
import com.dsige.reparto.dominion.data.viewModel.RepartoViewModel
import com.dsige.reparto.dominion.data.viewModel.ViewModelFactory
import com.dsige.reparto.dominion.helper.Gps
import com.dsige.reparto.dominion.helper.Util
import com.dsige.reparto.dominion.ui.listeners.OnItemClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.Observer
import kotlinx.android.synthetic.main.activity_reparto.*
import kotlinx.android.synthetic.main.activity_reparto.recyclerView
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

class RepartoActivity : DaggerAppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonSaved -> {
                if (cantidad > 0) {
                    repartoViewModel.getRegistroId(repartoId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Observer<Int> {
                            override fun onSubscribe(d: Disposable) {}
                            override fun onError(e: Throwable) {}
                            override fun onComplete() {}
                            override fun onNext(t: Int) {
                                startActivity(
                                    Intent(this@RepartoActivity, FormRepartoActivity::class.java)
                                        .putExtra("repartoId", t)
                                        .putExtra("recibo", barcode_code)
                                        .putExtra("operarioId", operarioId)
                                        .putExtra("cliente", cliente)
                                        .putExtra("validation", validation)
                                )
                                finish()
                            }
                        })
                } else repartoViewModel.setError("Se requiere foto")
            }
            R.id.imageView -> if (cantidad < 2) {
                startActivity(
                    Intent(this, CameraActivity::class.java)
                        .putExtra("repartoId", repartoId)
                        .putExtra("cuentaContrato", barcode_code)
                        .putExtra("tipo", 1)
                        .putExtra("direccion", direccion)
                )
            } else {
                Util.dialogMensaje(this, "Mensaje", "Maximo 2 fotos")
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var repartoViewModel: RepartoViewModel
    lateinit var play_formulario: MediaPlayer
    lateinit var play_normal: MediaPlayer
    lateinit var play_3: MediaPlayer
    lateinit var photoRepartoAdapter: PhotoAdapter

    private var cantidad: Int = 0
    private var barcode_code: String = ""
    private var Cod_Orden_Reparto: String = ""
    private var id_Cab_Reparto: Int = 0
    private var validation: Int = 0
    private var repartoId: Int = 0
    private var direccion: String = ""

    private var operarioId: Int = 0
    private var cliente: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reparto)

        val bundle = intent.extras
        if (bundle != null) {
            Cod_Orden_Reparto = bundle.getString("Cod_Orden_Reparto")!!
            id_Cab_Reparto = bundle.getInt("id_cab_Reparto")
            bindUI(bundle.getString("suministroNumeroReparto")!!)
        }
    }

    private fun bindUI(suministroNumeroReparto: String) {
        repartoViewModel =
            ViewModelProvider(this, viewModelFactory).get(RepartoViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "SuministroReparto"

        play_3 = MediaPlayer.create(this, R.raw.ic_error)
        play_formulario = MediaPlayer.create(this, R.raw.reparto_formulario)
        play_normal = MediaPlayer.create(this, R.raw.reparto_normal)

        photoRepartoAdapter = PhotoAdapter(object : OnItemClickListener.PhotoListener {
            override fun onItemClick(p: Photo, v: View, position: Int) {
                showPopupMenu(p, v)
            }
        })
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = photoRepartoAdapter

        buttonSaved.setOnClickListener(this)
        imageView.setOnClickListener(this)
        textViewTitle.text = getString(R.string.codigoManual)
        suministroReparto.text = String.format("Cuenta Contrato : %s", suministroNumeroReparto)

        editTextCodigoBarra.setOnEditorActionListener { p, p1, p2 ->
            val data = p.text
            if (data.isNotEmpty()) {
                var tipo = 0
                for (element in data) {
                    if (element.toString() == "?") {
                        tipo = 1
                        break
                    }
                    if (element.toString() == "_") {
                        tipo = 2
                        break
                    }
                }
                if (tipo == 1) {
                    val barcode = data.toString().split("?")
                    generateReparto(barcode[0])
                }
                if (tipo == 2) {
                    val barcode2 = data.toString().split("_")
                    generateReparto(barcode2[0])
                }
            }
            true
        }

        editTextCodigoBarra.isFocusableInTouchMode = true
        editTextCodigoBarra.requestFocus()

        repartoViewModel.getAllRegistro(1).observe(this, {
            if (it >= 10) {
                Util.executeRepartoWork(this)
            }
        })

        repartoViewModel.mensajeError.observe(this, {
            Util.toastMensaje(this, it)
        })
    }

    private fun generateReparto(barCode: String) {
        repartoViewModel.getCodigoBarra(barCode, 1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Reparto> {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onNext(t: Reparto) {
                    saveRegistroReparto(t)
                }

                override fun onError(e: Throwable) {
                    editTextCodigoBarra.text = null
                    cardViewDescripcion.visibility = View.GONE
                    play_3.start()
                }
            })
    }

    private fun saveRegistroReparto(r: Reparto) {
        val gps = Gps(this)
        if (gps.isLocationEnabled()) {
            if (gps.latitude.toString() == "0.0" || gps.longitude.toString() == "0.0") {
                gps.showAlert(this)
                return
            }
            textView.text = r.Cliente_Reparto
            textView1.text = r.Direccion_Reparto
            textView2.text = r.CodigoBarra
            textView3.text = r.Suministro_Medidor_reparto
            repartoId = r.id_Reparto
            direccion = r.Direccion_Reparto
            barcode_code = r.Suministro_Numero_reparto
            operarioId = r.id_Operario_Reparto
            cliente = r.Cliente_Reparto
            cardViewRegistro.visibility = View.GONE
            validation = r.foto_Reparto
            suministroReparto.text = String.format("Cuenta Contrato : %s", barcode_code)
            val registro = Registro(
                r.id_Reparto,
                r.id_Operario_Reparto,
                Util.getFechaActual(),
                gps.latitude.toString(),
                gps.longitude.toString(),
                r.id_observacion.toString(),
                if (validation == 0) 1 else 0
            )

            repartoViewModel.saveReparto(registro)
            cardViewDescripcion.visibility = View.VISIBLE
            editTextCodigoBarra.text = null
            Util.hideKeyboard(this)

            if (validation != 0) {
                bindListFoto(r.id_Reparto)
                cardViewRegistro.visibility = View.VISIBLE
                play_formulario.start()
                editTextCodigoBarra.visibility = View.GONE
            } else {
                play_normal.start()
            }
        } else {
            gps.showSettingsAlert(this)
        }
    }

    private fun bindListFoto(id: Int) {
        repartoViewModel.getPhotoReparto(id, 5).observe(this, {
            if (it != null) {
                cantidad = it.size
                photoRepartoAdapter.addItems(it)
            }
        })
    }

    private fun deletePhoto(p: Photo) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Mensaje")
            .setMessage("Desas eliminar foto ?")
            .setPositiveButton("SI") { dialog, _ ->
                repartoViewModel.deletePhoto(p, barcode_code, this)
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.cancel()
            }
        dialog.show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun showPopupMenu(p: Photo, v: View) {
        val popupMenu = PopupMenu(this, v)
        popupMenu.menu.add(0, Menu.FIRST, 0, getText(R.string.ver))
        popupMenu.menu.add(1, Menu.FIRST + 1, 1, getText(R.string.deletePhoto))
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    val intent = Intent(this, PreviewCameraActivity::class.java)
                    intent.putExtra("nameImg", p.rutaFoto)
                    intent.putExtra("repartoId", 0)
                    intent.putExtra("tipo", 0)
                    startActivity(intent)
                }
                2 -> {
                    deletePhoto(p)
                }
            }
            false
        }
        popupMenu.show()
    }
}