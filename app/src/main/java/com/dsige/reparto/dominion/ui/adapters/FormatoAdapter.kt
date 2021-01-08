package com.dsige.reparto.dominion.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.data.local.model.Formato
import com.dsige.reparto.dominion.ui.listeners.OnItemClickListener
import kotlinx.android.synthetic.main.cardview_combo.view.*

class FormatoAdapter(private val listener: OnItemClickListener.FormatoListener) :
        RecyclerView.Adapter<FormatoAdapter.ViewHolder>() {

    private var format = emptyList<Formato>()

    fun addItems(list: List<Formato>) {
        format = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_combo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(format[position], listener)
    }

    override fun getItemCount(): Int {
        return format.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(f: Formato, listener: OnItemClickListener.FormatoListener) = with(itemView) {
            textViewTitulo.text = String.format("%s",f.nombre)
            itemView.setOnClickListener { view -> listener.onItemClick(f, view, adapterPosition) }
        }
    }
}