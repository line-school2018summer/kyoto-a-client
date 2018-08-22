package intern.line.me.kyotoaclient.lib

import java.sql.Timestamp

class Room(
        var id: Long,
        var name: String,
        var createdAt: Timestamp,
        var updatedAt: Timestamp
)
