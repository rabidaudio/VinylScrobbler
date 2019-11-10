package audio.rabid.vinylscrobbler.data.models

import androidx.room.*
import okhttp3.HttpUrl
import java.time.Instant
import java.util.*

@Entity(tableName = "albums")
data class Album(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "artist_name")val artistName: String,
    @ColumnInfo(name = "mbid") val musicBrainzReleaseId: UUID?,
    @ColumnInfo(name = "cover_url") val coverUrl: HttpUrl?,
    @ColumnInfo(name = "last_listened_at") val lastListenedAt: Instant? = null
) {

    @androidx.room.Dao
    interface Dao {
        @Query("SELECT * FROM albums ORDER BY last_listened_at")
        suspend fun getAll(): List<Album>

        @Query("""
            SELECT * FROM albums
            WHERE name LIKE :name OR artist_name LIKE :name
            ORDER BY last_listened_at
            """)
        suspend fun search(name: String): List<Album>

        @Insert
        suspend fun add(album: Album)

        @Delete
        suspend fun delete(album: Album)
    }
}
