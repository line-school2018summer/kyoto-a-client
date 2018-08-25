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

class MessageList(val messages: MutableList<Message>) {
    var count: Int = messages.count()

    fun messageAt(index: Int): Message {
        return this.messages[index]
    }

    fun updateAt(index: Int, message: Message): Boolean {
        this.messages[index] = message
        return true
    }

    fun removeAt(index: Int): Boolean {
        this.messages.removeAt(index)
        this.updateCount()
        return true
    }

    fun add(message: Message): Boolean {
        messages.add(message)
        this.updateCount()
        return true
    }

    fun getLast(): Message {
        return this.messages[this.messages.lastIndex]
    }

    private fun updateCount() {
        this.count = this.messages.count()
    }
}
