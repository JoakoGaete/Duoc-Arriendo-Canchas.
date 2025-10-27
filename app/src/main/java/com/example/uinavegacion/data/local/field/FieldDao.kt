package com.example.uinavegacion.data.local.field

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface FieldDao {
    @Query("SELECT * FROM fields ") // Consulta SQL para obtener todas las canchas
    suspend fun getAllFields(): List<FieldEntity>

    @Query("SELECT * FROM fields WHERE id = :fieldId") // : indica que es una variable en la consulta
    fun getFieldById(fieldId: Int): Flow<FieldEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Si hay conflictos, reemplaza los existentes
    suspend fun insertAll(fields: List<FieldEntity>)
}

