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
    entities = [UserEntity::class, FieldEntity::class, BookingEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun fieldDao(): FieldDao
    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "arriendo_canchas.db"

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
                            CoroutineScope(Dispatchers.IO).launch {
                                // Usamos la instancia ya creada
                                val database = INSTANCE ?: return@launch
                                preloadUsers(database.userDao())
                                preloadFields(database.fieldDao())
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun preloadUsers(userDao: UserDao) {
            val seedUsers = listOf(
                UserEntity(
                    name = "Admin",
                    email = "admin@duoc.cl",
                    phone = "+56911111111",
                    password = "Admin123!",
                    isAdmin = true
                ),
                UserEntity(
                    name = "Víctor Rosendo",
                    email = "victor@duoc.cl",
                    phone = "+56922222222",
                    password = "123456",
                    isAdmin = false
                )
            )

            seedUsers.forEach { user ->
                // Inserta o reemplaza si ya existe
                userDao.insert(user)
            }
        }

        private suspend fun preloadFields(fieldDao: FieldDao) {
            val seedFields = listOf(
                FieldEntity(
                    id = 1,
                    name = "Cancha de Futbolito 1",
                    type = "Futbolito 7 vs 7 - Pasto Sintético",
                    location = "Sede Mall Plaza Norte",
                    pricePerHour = 17000.0,
                    imageUrl = "cancha1"
                ),
                FieldEntity(
                    id = 2,
                    name = "Cancha de Futbolito 2",
                    type = "Futbolito 7 vs 7 - Pasto Sintético",
                    location = "Sede Mall Plaza Norte",
                    pricePerHour = 18000.0,
                    imageUrl = "cancha2"
                ),
                FieldEntity(
                    id = 3,
                    name = "Cancha de Fútbol",
                    type = "Fútbol 11 vs 11 - Pasto Natural",
                    location = "Sede Mall Plaza Norte",
                    pricePerHour = 25000.0,
                    imageUrl = "cancha3"
                )
            )

            seedFields.forEach { field ->
                // Inserta o reemplaza si ya existe
                fieldDao.insertField(field)
            }
        }
    }
}

