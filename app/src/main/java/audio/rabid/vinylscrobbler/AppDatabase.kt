package audio.rabid.vinylscrobbler

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Album::class],
    version = 1
)
@TypeConverters(
    HttpUrlAdapter::class,
    UUIDAdapter::class,
    InstantAdapter::class
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
