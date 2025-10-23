// En: com/example/uinavegacion/data/local/database/AppDatabase.kt
package com.example.uinavegacion.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
// --- 1. IMPORTAR LO NUEVO ---
import com.example.uinavegacion.data.local.booking.BookingDao
import com.example.uinavegacion.data.local.booking.BookingEntity
import com.example.uinavegacion.data.local.field.FieldDao
import com.example.uinavegacion.data.local.field.FieldEntity
// --- FIN DE IMPORTS NUEVOS ---
import com.example.uinavegacion.data.local.user.UserDao
import com.example.uinavegacion.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    // Añadimos las nuevas entidades a la lista
    entities = [UserEntity::class, FieldEntity::class, BookingEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    //Dao de usuarios
    abstract fun userDao(): UserDao

    //Daos de canchas y reservas
    abstract fun fieldDao(): FieldDao
    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "ui_navegacion.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // La precarga ahora se hace en una corrutina
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)

                                // --- 4. PRECARGA DE DATOS ---
                                // A. Precarga de usuarios
                                preloadUsers(database.userDao())
                                // B. Precarga de canchas
                                preloadFields(database.fieldDao())
                            }
                        }
                    })
                    .fallbackToDestructiveMigration() // Mantenemos esto
                    .build()

                INSTANCE = instance
                instance
            }
        }


        // Precarga de usuarios
        private suspend fun preloadUsers(userDao: UserDao) {
            val seedUsers = listOf(
                UserEntity(
                    name = "Admin",
                    email = "admin@duoc.cl",
                    phone = "+56911111111",
                    password = "Admin123!"
                ),
                UserEntity(
                    name = "Víctor Rosendo",
                    email = "victor@duoc.cl",
                    phone = "+56922222222",
                    password = "123456"
                )
            )
            // Inserta solo si no hay usuarios
            if (userDao.count() == 0) {
                seedUsers.forEach { userDao.insert(it) }
            }
        }

        // Precarga de canchas
        private suspend fun preloadFields(fieldDao: FieldDao) {
            val seedFields = listOf(
                FieldEntity(
                    id = 1,
                    name = "Cancha de Futbolito 1",
                    type = "Futbolito 7 vs 7 - Pasto Sintético",
                    location = "Sede Mall Plaza Norte",
                    pricePerHour = 17000.0,
                    // Usa una URL de imagen real o el nombre de una imagen en tus recursos
                    imageUrl = "https://example.com/cancha1.jpg"
                ),
                FieldEntity(
                    id = 2,
                    name = "Cancha de Futbolito 2",
                    type = "Futbolito 7 vs 7 - Pasto Sintético",
                    location = "Sede Mall Plaza Norte",
                    pricePerHour = 18000.0,
                    imageUrl = "https://example.com/cancha2.jpg"
                ),
                FieldEntity(
                    id = 3,
                    name = "Cancha de Fútbol",
                    type = "Fútbol 11 vs 11 - Pasto Natural",
                    location = "Sede Mall Plaza Norte",
                    pricePerHour = 25000.0,
                    imageUrl = "https://example.com/cancha3.jpg"
                )
            )
            fieldDao.insertAll(seedFields)
        }
    }
}
