package com.dsige.reparto.dominion.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dsige.reparto.dominion.data.local.model.Photo

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhotoTask(c: Photo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhotoListTask(c: List<Photo>)

    @Update
    fun updatePhotoTask(vararg c: Photo)

    @Delete
    fun deletePhotoTask(c: Photo)

    @Query("SELECT * FROM Photo")
    fun getPhoto(): LiveData<Photo>

    @Query("SELECT * FROM Photo")
    fun getPhotoTask(): Photo

    @Query("DELETE FROM Photo")
    fun deleteAll()

    @Query("SELECT * FROM Photo WHERE iD_Suministro =:id AND tipo =:i")
    fun getPhotoReparto(id: Int, i: Int): LiveData<List<Photo>>

    @Query("SELECT * FROM Photo WHERE iD_Suministro =:id")
    fun getPhotoByFk(id: Int): List<Photo>

    @Query("SELECT * FROM Photo WHERE rutaFoto =:img")
    fun getPhotoByName(img: String): Photo?

    @Query("SELECT * FROM Photo WHERE estado = 1")
    fun getPhotos(): LiveData<List<Photo>>

    @Query("UPDATE Photo SET estado = 0 WHERE iD_Suministro=:id")
    fun closePhoto(id: Int)
}