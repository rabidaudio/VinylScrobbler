package audio.rabid.vinylscrobbler.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import audio.rabid.vinylscrobbler.core.adapters.*
import audio.rabid.vinylscrobbler.data.db.models.Album
import audio.rabid.vinylscrobbler.data.db.models.Track

@Database(
    entities = [
        Album::class,
        Track::class
    ],
    version = 2
)
@TypeConverters(
    HttpUrlAdapter::class,
    UUIDAdapter::class,
    InstantAdapter::class,
    LocalDateAdapter::class,
    DurationAdapter::class
)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        fun get(applicationContext: Context): AppDatabase {
            return Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app")
//                 .addMigrations()
                .build()
        }
    }

    abstract fun albumDao(): Album.Dao

    abstract fun trackDao(): Track.Dao
}
