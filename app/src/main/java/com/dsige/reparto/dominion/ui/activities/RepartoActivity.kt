package com.dsige.reparto.dominion.ui.activities

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RawRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsige.reparto.dominion.BuildConfig
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
import java.io.File
import java.util.*
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
                                        .putExtra("recibo", barcodeCode)
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
                formPhoto()
            } else {
                Util.dialogMensaje(this, "Mensaje", "Maximo 2 fotos")
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var repartoViewModel: RepartoViewModel
    private lateinit var photoRepartoAdapter: PhotoAdapter

    private var cantidad: Int = 0
    private var barcodeCode: String = ""
    private var codOrdenReparto: String = ""
    private var idCabReparto: Int = 0
    private var validation: Int = 0
    private var repartoId: Int = 0
    private var direccion: String = ""

    private var operarioId: Int = 0
    private var cliente: String = ""
    private var nameImg = ""
    private var latitud: String = ""
    private var longitud: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reparto)

        val bundle = intent.extras
        if (bundle != null) {
            codOrdenReparto = bundle.getString("Cod_Orden_Reparto")!!
            idCabReparto = bundle.getInt("id_cab_Reparto")
            bindUI(bundle.getString("suministroNumeroReparto")!!)
        }
    }

    private fun bindUI(suministroNumeroReparto: String) {
        repartoViewModel =
            ViewModelProvider(this, viewModelFactory).get(RepartoViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "SuministroReparto"

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

        editTextCodigoBarra.setOnEditorActionListener { p, _, _ ->
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
                    val str = barcode[0].replace(("[^\\d.]").toRegex(), "")
                    generateReparto(str)
                }
                if (tipo == 2) {
                    val barcode2 = data.toString().split("_")
                    val str = barcode2[0].replace(("[^\\d.]").toRegex(), "")
                    generateReparto(str)
                }
                Util.getTextStyleHtml(
                    String.format("<strong>Ultimo resultado:</strong> %s", data),
                    textViewResult
                )
            }
            true
        }

        editTextCodigoBarra.isFocusableInTouchMode = true
        editTextCodigoBarra.requestFocus()
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
                    playSound(R.raw.ic_error)
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
            barcodeCode = r.Suministro_Numero_reparto
            operarioId = r.id_Operario_Reparto
            cliente = r.Cliente_Reparto
            latitud = r.latitud
            longitud = r.longitud
            cardViewRegistro.visibility = View.GONE
            validation = r.foto_Reparto
            suministroReparto.text = String.format("Cuenta Contrato : %s", barcodeCode)
            val registro = Registro(
                r.id_Reparto,
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
                playSound(R.raw.reparto_formulario)
                editTextCodigoBarra.visibility = View.GONE
            } else {
                playSound(R.raw.reparto_normal)
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
            .setMessage("Deseas eliminar foto ?")
            .setPositiveButton("SI") { dialog, _ ->
                repartoViewModel.deletePhoto(p, barcodeCode, this)
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
                1 -> startActivity(
                    Intent(this, PreviewCameraActivity::class.java)
                        .putExtra("nameImg", p.rutaFoto)
                )
                2 -> deletePhoto(p)
            }
            false
        }
        popupMenu.show()
    }

    private fun formPhoto() {
        try {
            val gps = Gps(this)
            if (gps.isLocationEnabled()) {
                if (latitud.isEmpty()) {
                    latitud = gps.getLatitude().toString()
                }
                if (longitud.isEmpty()) {
                    longitud = gps.getLongitude().toString()
                }
                goCamera()
            } else {
                gps.showSettingsAlert(this)
            }
        } catch (e: Exception) {
            repartoViewModel.setError(e.toString())
        }

    }

    private fun goCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                nameImg = Util.getFechaActualForPhoto(barcodeCode, 5)
                val photoFile: File =
                    Util.createImageFile(nameImg, this)
                photoFile.also {
                    val uriSavedImage = FileProvider.getUriForFile(
                        this, BuildConfig.APPLICATION_ID + ".fileprovider", it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage)
                    if (Build.VERSION.SDK_INT >= 24) {
                        try {
                            val m =
                                StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                            m.invoke(null)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    resultLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                repartoViewModel.generatePhoto(
                    nameImg = nameImg,
                    context = this@RepartoActivity,
                    direccion = direccion,
                    latitud = latitud,
                    longitud = longitud,
                    id = repartoId
                )
            }
        }

    private val mediaPlayer = MediaPlayer().apply {
        setOnPreparedListener { start() }
        setOnCompletionListener { reset() }
    }

    fun playSound(@RawRes rawResId: Int) {
        val assetFileDescriptor = this.resources.openRawResourceFd(rawResId) ?: return
        mediaPlayer.run {
            reset()
            setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.declaredLength
            )
            prepareAsync()
        }
    }
}