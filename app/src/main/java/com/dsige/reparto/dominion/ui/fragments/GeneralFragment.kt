package com.dsige.reparto.dominion.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.data.local.model.Formato
import com.dsige.reparto.dominion.data.local.model.Recibo
import com.dsige.reparto.dominion.data.viewModel.RepartoViewModel
import com.dsige.reparto.dominion.data.viewModel.ViewModelFactory
import com.dsige.reparto.dominion.helper.Util
import com.dsige.reparto.dominion.ui.adapters.FormatoAdapter
import com.dsige.reparto.dominion.ui.listeners.OnItemClickListener
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_general.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"
private const val ARG_PARAM5 = "param5"

class GeneralFragment : DaggerFragment(), View.OnClickListener {

    override fun onClick(v: View) {
        Util.hideKeyboardFrom(requireContext(), v)
        when (v.id) {
            R.id.editTextVivienda -> dialogSpinner(2, "Vivienda")
            R.id.editTextColorFachada -> dialogSpinner(3, "Color/Fachada")
            R.id.editTextPuerta -> dialogSpinner(4, "Puerta")
            R.id.editTextColorPuerta -> dialogSpinner(5, "Color Puerta")
            R.id.editTextRecibido -> dialogSpinner(1, "Recibido")
            R.id.editTextDevuelto -> dialogSpinner(6, "Devuelto")
            R.id.fabGeneral -> validateGeneral()
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var repartoViewModel: RepartoViewModel
    lateinit var r: Recibo
    private var viewPager: ViewPager? = null

    private var repartoId: Int = 0
    private var recibo: String = ""
    private var operarioId: Int = 0
    private var cliente: String = ""
    private var validation: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        r = Recibo()
        arguments?.let {
            repartoId = it.getInt(ARG_PARAM1)
            recibo = it.getString(ARG_PARAM2)!!
            operarioId = it.getInt(ARG_PARAM3)
            cliente = it.getString(ARG_PARAM4)!!
            validation = it.getInt(ARG_PARAM5)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_general, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        repartoViewModel =
            ViewModelProvider(this, viewModelFactory).get(RepartoViewModel::class.java)

        editTextRecibo.setText(recibo)
        editTextCliente.setText(cliente)
        viewPager = requireActivity().findViewById(R.id.viewPager)

        repartoViewModel.getReciboByFk(repartoId).observe(viewLifecycleOwner, {
            if (it != null) {
                r = it
                editTextRecibido.setText(it.nombreformatoCargoRecibo)
                editTextVivienda.setText(it.nombreformatoVivienda)
                editTextColorFachada.setText(it.nombreformatoCargoColor)
                editTextPuerta.setText(it.nombreformatoCargoPuerta)
                editTextColorPuerta.setText(it.nombreformatoCargoColorPuerta)
                editTextDevuelto.setText(it.nombreformatoCargoDevuelto)
                editTextPiso.setText(it.piso.toString())
                editTextOtrosVivienda.setText(it.otrosVivienda)
                editTextOtrosColorFachada.setText(it.otrosCargoColor)
                editTextOtrosPuerta.setText(it.otrosCargoPuerta)
                editTextOtrosColorPuerta.setText(it.otrosCargoColorPuerta)
            }
        })


        repartoViewModel.mensajeError.observe(viewLifecycleOwner, {
            Util.toastMensaje(requireContext(), it)
        })
        repartoViewModel.mensajeSuccess.observe(viewLifecycleOwner, {
            if (it != null) {
                Util.hideKeyboardFrom(requireContext(), requireView())
                Util.toastMensaje(requireContext(), it)
                if (validation == 2) {
                    viewPager?.currentItem = 1
                } else {
                    repartoViewModel.updateRepartoEnvio(repartoId)
                }
            }
        })
        repartoViewModel.mensajeRecibo.observe(viewLifecycleOwner, {
            if (it != null) {
                requireActivity().finish()
            }
        })

        editTextVivienda.setOnClickListener(this)
        editTextColorFachada.setOnClickListener(this)
        editTextPuerta.setOnClickListener(this)
        editTextColorPuerta.setOnClickListener(this)
        editTextRecibido.setOnClickListener(this)
        editTextDevuelto.setOnClickListener(this)
        fabGeneral.setOnClickListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance(
            repartoId: Int, recibo: String, operarioId: Int, cliente: String, validation: Int
        ) =
            GeneralFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, repartoId)
                    putString(ARG_PARAM2, recibo)
                    putInt(ARG_PARAM3, operarioId)
                    putString(ARG_PARAM4, cliente)
                    putInt(ARG_PARAM5, validation)
                }
            }
    }

    private fun dialogSpinner(tipo: Int, title: String) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
        @SuppressLint("InflateParams") val v =
            LayoutInflater.from(context).inflate(R.layout.dialog_combo, null)
        val textViewTitulo: TextView = v.findViewById(R.id.textViewTitulo)
        val recyclerView: RecyclerView = v.findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(context)
        textViewTitulo.text = title
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()

        val formatAdapter = FormatoAdapter(object : OnItemClickListener.FormatoListener {
            override fun onItemClick(f: Formato, v: View, position: Int) {
                when (tipo) {
                    1 -> {
                        r.formatoCargoRecibo = f.formatoId
                        r.nombreformatoCargoRecibo = f.nombre
                        editTextRecibido.setText(f.nombre)
                    }
                    2 -> {
                        if (f.formatoId == 11) {
                            textViewOtrosVivienda.visibility = View.VISIBLE
                            Util.showKeyboard(editTextOtrosVivienda, context!!)
                        } else {
                            textViewOtrosVivienda.visibility = View.GONE
                            editTextOtrosVivienda.text = null
                        }
                        r.formatoVivienda = f.formatoId
                        r.nombreformatoVivienda = f.nombre
                        editTextVivienda.setText(f.nombre)
                    }
                    3 -> {
                        if (f.formatoId == 16) {
                            textViewOtrosColorFachada.visibility = View.VISIBLE
                            Util.showKeyboard(editTextOtrosColorFachada, context!!)
                        } else {
                            textViewOtrosColorFachada.visibility = View.GONE
                            editTextOtrosColorFachada.text = null
                        }
                        r.formatoCargoColor = f.formatoId
                        r.nombreformatoCargoColor = f.nombre
                        editTextColorFachada.setText(f.nombre)
                    }
                    4 -> {
                        if (f.formatoId == 20) {
                            textViewOtrosPuerta.visibility = View.VISIBLE
                            Util.showKeyboard(editTextOtrosPuerta, context!!)
                        } else {
                            textViewOtrosPuerta.visibility = View.GONE
                            editTextOtrosPuerta.text = null
                        }
                        r.formatoCargoPuerta = f.formatoId
                        r.nombreformatoCargoPuerta = f.nombre
                        editTextPuerta.setText(f.nombre)
                    }
                    5 -> {
                        if (f.formatoId == 25) {
                            textViewOtrosColorPuerta.visibility = View.VISIBLE
                            Util.showKeyboard(editTextOtrosColorPuerta, context!!)
                        } else {
                            textViewOtrosColorPuerta.visibility = View.GONE
                            editTextOtrosColorPuerta.text = null
                        }
                        r.formatoCargoColorPuerta = f.formatoId
                        r.nombreformatoCargoColorPuerta = f.nombre
                        editTextColorPuerta.setText(f.nombre)
                    }
                    6 -> {
                        r.formatoCargoDevuelto = f.formatoId
                        r.nombreformatoCargoDevuelto = f.nombre
                        editTextDevuelto.setText(f.nombre)
                    }
                }
                dialog.dismiss()
            }
        })
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = formatAdapter
        repartoViewModel.getFormato(tipo).observe(viewLifecycleOwner, {
            formatAdapter.addItems(it)
        })
    }

    private fun validateGeneral() {
        r.repartoId = repartoId
        r.operarioId = operarioId
        when {
            editTextPiso.text.toString().isEmpty() -> r.piso = 0
            else -> r.piso = editTextPiso.text.toString().toInt()
        }
        r.otrosVivienda = editTextOtrosVivienda.text.toString()
        r.otrosCargoColor = editTextOtrosColorFachada.text.toString()
        r.otrosCargoPuerta = editTextOtrosPuerta.text.toString()
        r.otrosCargoColorPuerta = editTextOtrosColorPuerta.text.toString()
        r.dniCargoRecibo = editTextDni.text.toString()
        r.parentesco = editTextParentesco.text.toString()
        r.observacionCargo = editTextObservaciones.text.toString()
        repartoViewModel.validateRegistroRecibo(r, validation)
    }

}