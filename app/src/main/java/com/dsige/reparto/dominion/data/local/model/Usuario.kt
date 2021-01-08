package com.dsige.reparto.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Usuario {

    @PrimaryKey
    var iD_Operario: Int = 0
    var operario_Login: String = ""
    var operario_Nombre: String = ""
    var operario_EnvioEn_Linea: Int = 0
    var tipoUsuario: String = ""
    var estado: String = ""
    var lecturaManual: Int = 0

    var vehiculoId: Int = 0
    var placa: String = ""
    var marca: String = ""
    var modelo: String = ""
    var anioFabricacion: String = ""
    var cilindrada: String = ""
    var inspeccionTecnica: String = ""
    var soat: String = ""
    var licenciaConducir: String = ""
    var constanciaVigente: String = ""

    var mensaje: String = ""
}