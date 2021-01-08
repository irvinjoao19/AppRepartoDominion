package com.dsige.reparto.dominion.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.data.local.model.Reparto
import com.dsige.reparto.dominion.helper.Util
import com.dsige.reparto.dominion.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_reparto.view.*
import java.util.*

open class RepartoAdapter(private val listener: OnItemClickListener.RepartoListener) :
    RecyclerView.Adapter<RepartoAdapter.ViewHolder>() {

    private var repartos = emptyList<Reparto>()
    private var repartosList: ArrayList<Reparto> = ArrayList()

    fun addItems(list: List<Reparto>) {
        repartos = list
        repartosList = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_reparto, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(repartosList[position], listener)
    }

    override fun getItemCount(): Int {
        return repartosList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(r: Reparto, listener: OnItemClickListener.RepartoListener) =
            with(itemView) {
                Util.getTextStyleHtml(
                    String.format("<strong>Orden : </strong>%s", r.Cod_Orden_Reparto),
                    textViewOrden
                )
                textViewDireccion.text = r.Direccion_Reparto
                Util.getTextStyleHtml(
                    String.format("<strong>Suministro : </strong>%s", r.Suministro_Numero_reparto),
                    textViewSuministro
                )
                imageViewMap.setOnClickListener { v -> listener.onItemClick(r, v, adapterPosition) }
                itemView.setOnClickListener { v -> listener.onItemClick(r, v, adapterPosition) }
            }
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                return FilterResults()
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                repartosList.clear()
                val keyword = charSequence.toString()
                if (keyword.isEmpty()) {
                    repartosList.addAll(repartos)
                } else {
                    val filteredList = ArrayList<Reparto>()
                    for (r: Reparto in repartos) {
                        if (r.Cliente_Reparto.toLowerCase(Locale.getDefault()).contains(keyword) ||
                            r.Direccion_Reparto.toLowerCase(Locale.getDefault())
                                .contains(keyword) ||
                            r.Suministro_Numero_reparto.contains(keyword) ||
                            r.Suministro_Medidor_reparto.contains(keyword)
                        ) {
                            filteredList.add(r)
                        }
                    }
                    repartosList = filteredList
                }
                notifyDataSetChanged()
            }
        }
    }
}