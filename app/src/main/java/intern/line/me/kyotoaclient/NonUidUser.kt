package intern.line.me.kyotoaclient

import java.sql.Timestamp

data class NonUidUser(
        var id: Long,
        var name: String,
        var createdAt: Timestamp,
        var updatedAt: Timestamp
)