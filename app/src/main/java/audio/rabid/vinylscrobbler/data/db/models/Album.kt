package audio.rabid.vinylscrobbler.data.db.models

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import kotlinx.coroutines.flow.Flow
import okhttp3.HttpUrl
import java.time.Instant
import java.util.*

@Entity(
    tableName = "albums",
    indices = [
        Index(value = ["mbid"], unique = true)
    ]
)
data class Album(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "artist_name")val artistName: String,
    @ColumnInfo(name = "mbid") val musicBrainzReleaseId: UUID?,
    @ColumnInfo(name = "cover_url") val coverUrl: HttpUrl?,
    @ColumnInfo(name = "last_listened_at") val lastListenedAt: Instant? = null
) {

    @androidx.room.Dao
    interface Dao {
        @Query("SELECT * FROM albums ORDER BY last_listened_at")
        fun getAll(): Flow<List<Album>>

        @Query("""
            SELECT * FROM albums
            WHERE name LIKE :name OR artist_name LIKE :name
            ORDER BY last_listened_at
        """)
        fun search(name: String): Flow<List<Album>>

        @Query("SELECT * FROM albums where id = :albumId")
        suspend fun find(albumId: Long): Album

        @Update
        suspend fun update(album: Album)

        @Insert(onConflict = REPLACE)
        suspend fun insert(album: Album): Long

        @Delete
        suspend fun delete(album: Album)
    }
}
