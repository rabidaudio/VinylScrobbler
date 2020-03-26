package audio.rabid.vinylscrobbler.data.db.models

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import java.time.Duration
import java.util.*

@Entity(
    tableName = "tracks",
    indices = [
        Index(value = ["album_id", "position"], unique = true),
        Index(value = ["mbid"], unique = true)
    ]
)
data class Track(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "artist_name") val artistName: String,
    @ColumnInfo(name = "album_name") val albumName: String,
    @ColumnInfo(name = "position") val position: Int,
    @ColumnInfo(name = "track_number") val trackNumber: String,
    @ColumnInfo(name = "mbid") val musicBrainzTrackId: UUID?,
    @ColumnInfo(name = "duration") val duration: Duration,
    @ColumnInfo(name = "album_id") val albumId: Long
) {

    @androidx.room.Dao
    interface Dao {
        @Query("SELECT * FROM tracks where album_id = :albumId ORDER BY position")
        suspend fun getTracksForAlbum(albumId: Int): List<Track>

        @Query("SELECT * FROM tracks where id = :trackId")
        suspend fun find(trackId: Long): Track

        @Query("SELECT * FROM tracks where id IN (:trackIds)")
        suspend fun find(trackIds: List<Long>): List<Track>

        @Insert(onConflict = REPLACE)
        suspend fun insert(tracks: List<Track>)

        @Delete
        suspend fun delete(tracks: List<Track>)
    }
}
