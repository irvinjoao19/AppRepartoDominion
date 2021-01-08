package com.dsige.reparto.dominion.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.data.local.model.MenuPrincipal

class SendAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<SendAdapter.ViewHolder>() {

    private var menus = emptyList<MenuPrincipal>()

    fun addItems(list: List<MenuPrincipal>) {
        menus = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(menus[position], listener)
    }

    override fun getItemCount(): Int {
        return menus.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewPhoto: ImageView = itemView.findViewById(R.id.imageViewPhoto)
        private val textViewTitulo: TextView = itemView.findViewById(R.id.textViewTitulo)
        private val textViewCount: TextView = itemView.findViewById(R.id.textViewCount)

        fun bind(m: MenuPrincipal, listener: OnItemClickListener) {
            if (m.menuId == 3) {
                textViewCount.visibility = View.GONE
            } else {
                textViewCount.text = m.cantidad.toString()
                textViewCount.visibility = View.VISIBLE
            }
            textViewTitulo.text = m.title
            imageViewPhoto.setImageResource(m.imagen)
            itemView.setOnClickListener { listener.onItemClick(m, adapterPosition) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(m: MenuPrincipal, position: Int)
    }
}