package com.dsige.reparto.dominion.data.local.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
open class Registro(
    var iD_Suministro: Int,
    var iD_Registro: Int,
    var iD_Operario: Int,
    var registro_Fecha_SQLITE: String,
    var registro_Latitud: String,
    var registro_Longitud: String,
    var registro_Observacion: String,
    var estado: Int
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var suministro_Numero: Int = 0
    var iD_TipoLectura: Int = 0
    var registro_Lectura: String = ""
    var registro_Confirmar_Lectura: String = ""
    var grupo_Incidencia_Codigo: String = ""
    var registro_TieneFoto: String = ""
    var registro_TipoProceso: String = ""
    var fecha_Sincronizacion_Android: String = ""
    var registro_Constancia: String = ""
    var registro_Desplaza: String = ""
    var codigo_Resultado: String = ""
    var tipo: Int = 0
    var orden: Int = 0
    var horaActa: String = ""
    var suministroCliente: String = ""
    var suministroDireccion: String = ""
    var lecturaManual: Int = 0
    var motivoId: Int = 0
    var parentId: Int = 0

    @Ignore
    var photos: List<Photo> = ArrayList()
    @Ignore
    var recibo: Recibo? = null

}