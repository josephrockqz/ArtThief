package com.example.artthief.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ArtworksDao {
    @Query("select * from databaseartwork")
    fun getVideos(): LiveData<List<DatabaseArtwork>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( videos: List<DatabaseArtwork>)
}



@Database(entities = [DatabaseArtwork::class], version = 1)
abstract class ArtworksDatabase: RoomDatabase() {
    abstract val artworkDao: ArtworksDao
}

private lateinit var INSTANCE: ArtworksDatabase

fun getDatabase(context: Context): ArtworksDatabase {
    synchronized(ArtworksDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                ArtworksDatabase::class.java,
                "artworks").build()
        }
    }
    return INSTANCE
}
