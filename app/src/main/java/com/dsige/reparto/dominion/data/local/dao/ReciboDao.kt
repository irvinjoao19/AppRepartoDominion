package com.dsige.reparto.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.reparto.dominion.data.local.model.Recibo

@Dao
interface ReciboDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReciboTask(c: Recibo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReciboListTask(c: List<Recibo>)

    @Update
    fun updateReciboTask(vararg c: Recibo)

    @Delete
    fun deleteReciboTask(c: Recibo)

    @Query("SELECT * FROM Recibo")
    fun getReciboTask(): Recibo

    @Query("DELETE FROM Recibo")
    fun deleteAll()

    @Query("SELECT * FROM Recibo WHERE reciboId =:id")
    fun getReciboById(id: Int): Recibo?

    @Query("SELECT * FROM Recibo WHERE repartoId =:id")
    fun getReciboTaskByFk(id: Int): Recibo?

    @Query("SELECT * FROM Recibo WHERE repartoId =:id")
    fun getReciboByFk(id: Int): LiveData<Recibo>

    @Query("UPDATE Recibo SET firmaCliente=:name WHERE repartoId =:id")
    fun updateFirma(id: Int, name: String)

    @Query("SELECT * FROM Recibo WHERE repartoId =:id")
    fun getRecibos(id: Int): LiveData<List<Recibo>>
}