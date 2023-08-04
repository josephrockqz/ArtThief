package com.example.artthief.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ArtworksDao {
    @Query("select * from databaseArtwork")
    fun getArtworks(): LiveData<List<DatabaseArtwork>>

    @Query("select * from databaseArtwork where artThiefID = :artThiefID")
    fun getArtworkById(artThiefID: Int): DatabaseArtwork

    /**
     * On conflict strategy is ignore. This way, if an artwork has already been given
     * a rating, its rating will not be lost
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(
        artworks: List<DatabaseArtwork>
    )

    /**
     * On conflict strategy is replace. The intent is to replace the row (artwork)
     * entry in the database with an updated rating
     */
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
