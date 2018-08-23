package intern.line.me.kyotoaclient.lib

import java.sql.Timestamp

class Room(
        var id: Long,
        var name: String,
        var createdAt: Timestamp,
        var updatedAt: Timestamp,
        var latestMessage: String,
        var messageTime: Timestamp
)

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