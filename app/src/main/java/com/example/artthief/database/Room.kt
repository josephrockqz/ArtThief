package com.example.artthief.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ArtworksDao {
    @Query("select * from databaseArtwork")
    fun getArtworks(): LiveData<List<DatabaseArtwork>>

    /**
     * On conflict strategy is ignore. This way, if an artwork has already been given
     * a rating, its rating will not be lost
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(
        artworks: List<DatabaseArtwork>
    )

    // TODO: check to see this actually works
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(
        artwork: DatabaseArtwork
    )
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
