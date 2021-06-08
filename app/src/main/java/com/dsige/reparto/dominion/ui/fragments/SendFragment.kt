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
import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.data.viewModel.RepartoViewModel
import com.dsige.reparto.dominion.data.viewModel.ViewModelFactory
import com.dsige.reparto.dominion.helper.Util
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_send.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SendFragment : DaggerFragment(), View.OnClickListener {

    override fun onClick(v: View) {
        confirmSend()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var repartoViewModel: RepartoViewModel
    lateinit var builder: AlertDialog.Builder
    private var dialog: AlertDialog? = null
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_send, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        repartoViewModel =
            ViewModelProvider(this, viewModelFactory).get(RepartoViewModel::class.java)

        repartoViewModel.getRegistros().observe(viewLifecycleOwner) {
            if (it != null) {
                imgBadge.badgeValue = it.size
                txt1.text = String.format("Registros : %s", it.size)
            }
        }

        repartoViewModel.getPhotos().observe(viewLifecycleOwner) {
            if (it != null) {
                txt2.text = String.format("Fotos : %s", it.size)
            }
        }

        repartoViewModel.mensajeSuccess.observe(viewLifecycleOwner) {
            closeLoad()
            Util.toastMensaje(requireContext(), it)
        }

        repartoViewModel.mensajeError.observe(viewLifecycleOwner) {
            closeLoad()
            Util.toastMensaje(requireContext(), it)
        }

        btnEnvio.setOnClickListener(this)
    }

    private fun confirmSend() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Mensaje")
            .setMessage(
                String.format(
                    "Antes de enviar asegurate de contar con internet !.%s",
                    "\nDeseas enviar los Registros ?."
                )
            )
            .setPositiveButton("Aceptar") { dialog, _ ->
                load()
                repartoViewModel.sendFiles(requireContext())
                dialog.dismiss()
            }.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun load() {
        builder = AlertDialog.Builder(ContextThemeWrapper(requireContext(), R.style.AppTheme))
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_login, null)
        val textViewTitle: TextView = view.findViewById(R.id.textView)
        builder.setView(view)
        textViewTitle.text = String.format("Enviando...")
        dialog = builder.create()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    private fun closeLoad() {
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SendFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}