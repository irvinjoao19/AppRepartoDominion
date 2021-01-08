package com.dsige.reparto.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Parametro {
    @PrimaryKey
    var id_Configuracion: Int = 0
    var nombre_parametro: String = ""
    var valor: Int = 0
}
