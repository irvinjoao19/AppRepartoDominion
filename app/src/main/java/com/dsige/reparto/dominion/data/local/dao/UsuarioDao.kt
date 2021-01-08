package com.dsige.reparto.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.reparto.dominion.data.local.model.Usuario

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsuarioTask(c: Usuario)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsuarioListTask(c: List<Usuario>)

    @Update
    fun updateUsuarioTask(vararg c: Usuario)

    @Delete
    fun deleteUsuarioTask(c: Usuario)

    @Query("SELECT * FROM Usuario")
    fun getUsuario(): LiveData<Usuario>

    @Query("SELECT * FROM Usuario")
    fun getUsuarioTask(): Usuario

    @Query("DELETE FROM Usuario")
    fun deleteAll()

    @Query("SELECT iD_Operario FROM Usuario")
    fun getUsuarioIdTask(): Int
}