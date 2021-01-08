package com.dsige.reparto.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.reparto.dominion.data.local.model.Formato

@Dao
interface FormatoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFormatoTask(c: Formato)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFormatoListTask(c: List<Formato>)

    @Update
    fun updateFormatoTask(vararg c: Formato)

    @Delete
    fun deleteFormatoTask(c: Formato)

    @Query("SELECT * FROM Formato WHERE tipo =:t")
    fun getFormato(t: Int): LiveData<List<Formato>>

    @Query("SELECT * FROM Formato")
    fun getFormatoTask(): Formato

    @Query("DELETE FROM Formato")
    fun deleteAll()
}