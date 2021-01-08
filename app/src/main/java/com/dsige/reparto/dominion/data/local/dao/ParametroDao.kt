package com.dsige.reparto.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.reparto.dominion.data.local.model.Parametro

@Dao
interface ParametroDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertParametroTask(c: Parametro)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertParametroListTask(c: List<Parametro>)

    @Update
    fun updateParametroTask(vararg c: Parametro)

    @Delete
    fun deleteParametroTask(c: Parametro)

    @Query("SELECT * FROM Parametro")
    fun getParametro(): LiveData<Parametro>

    @Query("SELECT * FROM Parametro")
    fun getParametroTask(): Parametro

    @Query("DELETE FROM Parametro")
    fun deleteAll()
}