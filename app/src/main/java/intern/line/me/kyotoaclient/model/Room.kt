package intern.line.me.kyotoaclient.model

import java.sql.Timestamp
import java.io.Serializable

class Room(

        var id: Long,
        var name: String,
        var created_at: Timestamp,
        var updated_at: Timestamp,
        
        var last_message: Message?
): Serializable

class RoomList(private val rooms: MutableList<Room>) {
    var count: Int = rooms.count()

    fun roomAt(index: Int): Room {
        return this.rooms[index]
    }

    fun updateAt(index: Int, room: Room): Boolean {
        this.rooms[index] = room
        return true
    }

    fun add(room: Room): Boolean {
        rooms.add(room)
        this.updateCount()
        return true
    }

    private fun updateCount() {
        this.count = this.rooms.count()
    }
}