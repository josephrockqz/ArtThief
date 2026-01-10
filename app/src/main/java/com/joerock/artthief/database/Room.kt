package com.joerock.artthief.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

@Dao
interface ArtworksDao {
    @Query("select * from databaseArtwork")
    fun getArtworks(): LiveData<List<DatabaseArtwork>>

    @Query("select * from databaseArtwork where artThiefID = :artThiefID")
    fun getArtworkById(artThiefID: Int): DatabaseArtwork

    /**
     * On conflict strategy is ignore. This way, if an artwork has already been given
     * a rating, its rating will not be lost
     *
     * NOT currently being used
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

    @Query("delete from databaseArtwork where artThiefID not in (:artworksIdList)")
    fun deleteArtworksById(
        artworksIdList: List<Int>
    )
}

@Database(entities = [DatabaseArtwork::class], version = 2, exportSchema = false)
abstract class ArtworksDatabase: RoomDatabase() {
    abstract val artworkDao: ArtworksDao
}

private lateinit var INSTANCE: ArtworksDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE databaseArtwork ADD COLUMN artistUrl TEXT")
    }
}

fun getDatabase(context: Context): ArtworksDatabase {
    synchronized(ArtworksDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                ArtworksDatabase::class.java,
                "artworks")
                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }
    return INSTANCE
}
