package com.dsige.reparto.dominion.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dsige.reparto.dominion.R
import com.dsige.reparto.dominion.data.local.model.Photo
import com.dsige.reparto.dominion.helper.Util
import com.dsige.reparto.dominion.ui.adapters.PhotoAdapter.ViewHolder
import com.dsige.reparto.dominion.ui.listeners.OnItemClickListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cardview_photo.view.*
import java.io.File

class PhotoAdapter(private var listener: OnItemClickListener.PhotoListener) :
    RecyclerView.Adapter<ViewHolder>() {

    private var photos = emptyList<Photo>()

    fun addItems(list: List<Photo>) {
        photos = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_photo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photos[position], listener)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(photo: Photo, listener: OnItemClickListener.PhotoListener) = with(itemView) {
            val f = File(Util.getFolder(itemView.context), photo.rutaFoto)
            Picasso.get().load(f).into(imageViewPhoto)
            textViewName.text = photo.rutaFoto
            itemView.setOnClickListener { v -> listener.onItemClick(photo, v, adapterPosition) }
        }
    }
}