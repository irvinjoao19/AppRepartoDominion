package com.dsige.reparto.dominion.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Reparto {
    @PrimaryKey
    var id_Reparto: Int = 0
    var id_Operario_Reparto: Int = 0
    var foto_Reparto: Int = 0
    var id_observacion: Int = 0
    var Suministro_Medidor_reparto: String = ""
    var Suministro_Numero_reparto: String = ""
    var Direccion_Reparto: String = ""
    var Cod_Orden_Reparto: String = ""
    var Cod_Actividad_Reparto: String = ""
    var Cliente_Reparto: String = ""
    var CodigoBarra: String = ""
    var estado: Int = 0
    var activo: Int = 0
    var latitud: String = ""
    var longitud: String = ""
    var isActive: Boolean = false
}