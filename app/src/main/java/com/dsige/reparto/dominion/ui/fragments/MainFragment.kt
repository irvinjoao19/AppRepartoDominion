package com.dsige.reparto.dominion.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsige.reparto.dominion.ui.adapters.RepartoAdapter
import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.data.local.model.Reparto
import com.dsige.reparto.dominion.data.viewModel.RepartoViewModel
import com.dsige.reparto.dominion.data.viewModel.ViewModelFactory
import com.dsige.reparto.dominion.helper.Util
import com.dsige.reparto.dominion.ui.activities.MapsActivity
import com.dsige.reparto.dominion.ui.activities.PendingLocationMapsActivity
import com.dsige.reparto.dominion.ui.activities.RepartoActivity
import com.dsige.reparto.dominion.ui.listeners.OnItemClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

private const val ARG_PARAM1 = "param1"

class MainFragment : DaggerFragment(), View.OnClickListener {

    override fun onClick(v: View) {
        mensajeRuta(Reparto(), false)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var repartoViewModel: RepartoViewModel
    lateinit var repartoAdapter: RepartoAdapter
    private var usuarioId: Int = 0


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                val searchView = item.actionView as SearchView
                search(searchView)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuarioId = it.getInt(ARG_PARAM1)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        repartoViewModel =
            ViewModelProvider(this, viewModelFactory).get(RepartoViewModel::class.java)

        repartoAdapter = RepartoAdapter(object : OnItemClickListener.RepartoListener {
            override fun onItemClick(r: Reparto, v: View, position: Int) {
                when (v.id) {
                    R.id.imageViewMap -> {
                        if (r.latitud.isNotEmpty() || r.longitud.isNotEmpty()) {
                            mensajeRuta(r, true)
                        } else {
                            Util.toastMensaje(
                                requireContext(),
                                "Este suministro no cuenta con coordenadas"
                            )
                        }
                    }
                    else -> {
                        startActivity(
                            Intent(requireContext(), RepartoActivity::class.java)
                                .putExtra("Cod_Orden_Reparto", r.Cod_Orden_Reparto)
                                .putExtra("id_cab_Reparto", r.id_Reparto)
                                .putExtra("suministroNumeroReparto", r.Suministro_Numero_reparto)
                        )
                    }
                }
            }
        })
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = repartoAdapter

        repartoViewModel.getRepartos().observe(viewLifecycleOwner, {
            repartoAdapter.addItems(it)
        })

        fabMap.setOnClickListener(this)
    }

    private fun mensajeRuta(r: Reparto, b: Boolean) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Mensaje")
            .setMessage(
                if (b)
                    String.format("Deseas visualizar RUTA ?") else String.format("Deseas ver las ubicaciones ?")
            )
            .setPositiveButton("Aceptar") { dialog, _ ->
                if (b)
                    startActivity(
                        Intent(requireContext(), MapsActivity::class.java)
                            .putExtra("latitud", r.latitud)
                            .putExtra("longitud", r.longitud)
                            .putExtra("title", r.Suministro_Numero_reparto)
                    )
                else
                    startActivity(Intent(requireContext(), PendingLocationMapsActivity::class.java))

                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
        dialog.show()
    }


    private fun search(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                repartoAdapter.getFilter().filter(newText)
                return true
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                }
            }
    }
}