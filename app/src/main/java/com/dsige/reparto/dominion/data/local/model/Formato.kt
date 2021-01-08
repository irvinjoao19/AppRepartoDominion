package com.dsige.reparto.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Formato {
    @PrimaryKey
    var formatoId: Int = 0
    var tipo: Int = 0
    var nombre: String = ""
    var abreviatura: String = ""
    var estado: Int = 0
}