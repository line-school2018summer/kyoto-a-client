package intern.line.me.kyotoaclient.lib

import java.sql.Timestamp

class User(
        var id: Long,
        var name: String,
        var createdAt: Timestamp,
        var updatedAt: Timestamp
)

class UserList(private val users: MutableList<User>) {
    var count: Int = users.count()

    fun userAt(index: Int): User {
        return this.users[index]
    }

    fun updateAt(index: Int, user: User): Boolean {
        this.users[index] = user
        return true
    }

    fun add(room: User): Boolean {
        users.add(room)
        this.updateCount()
        return true
    }

    private fun updateCount() {
        this.count = this.users.count()
    }
}