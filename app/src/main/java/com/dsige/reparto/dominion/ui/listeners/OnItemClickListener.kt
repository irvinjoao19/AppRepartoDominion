package com.dsige.reparto.dominion.ui.listeners

import android.view.View
import com.dsige.reparto.dominion.data.local.model.*

interface OnItemClickListener {
    interface RepartoListener {
        fun onItemClick(r: Reparto, v: View, position: Int)
    }

    interface MenuListener {
        fun onItemClick(m: MenuPrincipal, v: View, position: Int)
    }

    interface FormatoListener {
        fun onItemClick(f: Formato, v: View, position: Int)
    }

    interface ServicioListener {
        fun onItemClick(s: Servicio, position: Int)
    }

    interface PhotoListener {
        fun onItemClick(p: Photo, v: View, position: Int)
    }
}