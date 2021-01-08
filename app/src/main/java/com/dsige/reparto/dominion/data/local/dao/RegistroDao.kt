package com.dsige.reparto.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.reparto.dominion.data.local.model.Registro

@Dao
interface RegistroDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRegistroTask(c: Registro)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRegistroListTask(c: List<Registro>)

    @Update
    fun updateRegistroTask(vararg c: Registro)

    @Delete
    fun deleteRegistroTask(c: Registro)

    @Query("SELECT * FROM Registro WHERE estado =:e")
    fun getRegistroTask(e:Int): List<Registro>

    @Query("DELETE FROM Registro")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM Registro WHERE estado=:i")
    fun getAllRegistro(i: Int): LiveData<Int>

    @Query("SELECT id FROM Registro WHERE iD_Suministro=:id")
    fun getRegistroId(id: Int): Int

    @Query("UPDATE Registro SET estado = 0 WHERE iD_Registro=:id")
    fun closeRegistro(id: Int)

    @Query("UPDATE Registro SET estado = 1 WHERE id=:id")
    fun activeRegistro(id: Int)

    @Query("SELECT * FROM Registro WHERE id=:i")
    fun getRegistroIdTask(i: Int): Registro

    @Query("SELECT * FROM Registro WHERE iD_Suministro=:i")
    fun getValidateRegistroIdTask(i: Int): Registro


}