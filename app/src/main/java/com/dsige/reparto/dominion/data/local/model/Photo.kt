package com.dsige.reparto.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Photo(
    //se usara para mostrar la foto de acta de conformidad 0 = normal , 1 = acta , 2 = firma
    var conformidad: Int,
    var iD_Suministro: Int,
    var rutaFoto: String,
    var fecha_Sincronizacion_Android: String,
    var tipo: Int,
    var estado: Int,
    var latitud: String,
    var longitud: String
) {
    @PrimaryKey(autoGenerate = true)
    var iD_Foto: Int = 0
    var firm: Int = 0
    var tipoFirma: String = ""

}