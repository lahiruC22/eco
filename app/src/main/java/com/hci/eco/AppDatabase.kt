package com.hci.eco

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity(tableName = "image_metadata")
data class ImageMetaData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUri: String,
    val timeStamp: String
)

@Dao
interface ImageMetaDataDao {
    @Insert
    suspend fun insert(imageMetaData: ImageMetaData)

    @Query("SELECT * FROM image_metadata")
    suspend fun getAllImages(): List<ImageMetaData>
}

@Database(entities = [ImageMetaData::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageMetaDataDao(): ImageMetaDataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "image_metadata_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}