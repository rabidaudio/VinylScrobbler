package audio.rabid.vinylscrobbler.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import audio.rabid.vinylscrobbler.core.adapters.HttpUrlAdapter
import audio.rabid.vinylscrobbler.core.adapters.InstantAdapter
import audio.rabid.vinylscrobbler.core.adapters.LocalDateAdapter
import audio.rabid.vinylscrobbler.core.adapters.UUIDAdapter
import audio.rabid.vinylscrobbler.data.db.models.Album

@Database(
    entities = [Album::class],
    version = 1
)
@TypeConverters(
    HttpUrlAdapter::class,
    UUIDAdapter::class,
    InstantAdapter::class,
    LocalDateAdapter::class
)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        fun get(applicationContext: Context): AppDatabase {
            return Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app")
                // .addMigrations()
                .build()
        }
    }

    abstract fun albumDao(): Album.Dao
}
