package intern.line.me.kyotoaclient.lib

import java.sql.Timestamp

class Message(
    var id: Long,
    var room_id: Long,
    var user_id: Long,
    var text: String,
    var user: User,
    var created_at: Timestamp,
    var updated_at: Timestamp
)

