package com.dsige.reparto.dominion.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsige.reparto.dominion.ui.adapters.FirmRepartoAdapter
import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.data.local.model.MenuPrincipal
import com.dsige.reparto.dominion.data.local.model.Recibo
import com.dsige.reparto.dominion.data.viewModel.RepartoViewModel
import com.dsige.reparto.dominion.data.viewModel.ViewModelFactory
import com.dsige.reparto.dominion.ui.activities.FirmActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_firm.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"

class FirmFragment : DaggerFragment(), View.OnClickListener {

    override fun onClick(v: View) {
        fabSave.visibility = View.VISIBLE
        fabFirm.visibility = View.GONE
        when (v.id) {
            R.id.fabFirm -> if (validate != 0) {
                startActivity(
                    Intent(requireContext(), FirmActivity::class.java)
                        .putExtra("repartoId", repartoId)
                )
            } else repartoViewModel.setError("Completar el primer formulario")
            R.id.fabSave -> {
                repartoViewModel.updateRepartoEnvio(repartoId)
//                context!!.stopService(Intent(context!!, AlertRepartoSleepService::class.java))
//                context!!.startService(Intent(context!!, DistanceService::class.java))
                requireActivity().finish()
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var repartoViewModel: RepartoViewModel
    private var repartoId: Int = 0
    private var validate: Int = 0

    lateinit var firmAdapter: FirmRepartoAdapter
    lateinit var r: Recibo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        r = Recibo()
        arguments?.let {
            repartoId = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_firm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        repartoViewModel =
            ViewModelProvider(this, viewModelFactory).get(RepartoViewModel::class.java)

        repartoViewModel.getReciboByFk(repartoId).observe(viewLifecycleOwner, {
            if (it != null) {
                r = it
                validate = 1
            }
        })

        val layoutManager = LinearLayoutManager(context)
        firmAdapter = FirmRepartoAdapter()
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = firmAdapter
        repartoViewModel.getRecibos(repartoId).observe(viewLifecycleOwner, {
            getFirm(it)
        })

        fabFirm.setOnClickListener(this)
        fabSave.setOnClickListener(this)
    }

    private fun getFirm(list: List<Recibo>) {
        if (list.isNotEmpty()) {
            val r: Recibo? = list[0]
            if (r != null) {
                if (r.firmaCliente.isNotEmpty()) {
                    val firm = ArrayList<MenuPrincipal>()
                    firm.add(MenuPrincipal(1, r.firmaCliente, 1, 0))
                    firmAdapter.addItems(firm)
                    fabSave.visibility = View.VISIBLE
                    fabFirm.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(repartoId: Int) =
            FirmFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, repartoId)
                }
            }
    }
}
