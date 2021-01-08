package com.dsige.reparto.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.reparto.dominion.data.local.model.Reparto

@Dao
interface RepartoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRepartoTask(c: Reparto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRepartoListTask(c: List<Reparto>)

    @Update
    fun updateRepartoTask(vararg c: Reparto)

    @Delete
    fun deleteRepartoTask(c: Reparto)

    @Query("SELECT * FROM Reparto WHERE activo = 1 ")
    fun getRepartos(): LiveData<List<Reparto>>

    @Query("SELECT * FROM Reparto")
    fun getRepartoTask(): Reparto

    @Query("DELETE FROM Reparto")
    fun deleteAll()

    @Query("SELECT * FROM Reparto WHERE id_Reparto =:id")
    fun getRepartoById(id: Int): LiveData<Reparto>

    @Query("SELECT * FROM Reparto WHERE Suministro_Numero_reparto =:barCode AND activo=:i ")
    fun getCodigoBarra(barCode: String, i: Int): Reparto

    @Query("UPDATE Reparto SET activo = 0 WHERE id_Reparto =:id")
    fun disableReparto(id: Int)

    @Query("UPDATE Reparto SET activo = 1 WHERE Suministro_Numero_reparto =:code")
    fun enableReparto(code: String)

    @Query("UPDATE Reparto SET activo = 0 WHERE id_Reparto =:id")
    fun updateReparto(id: Int)
}