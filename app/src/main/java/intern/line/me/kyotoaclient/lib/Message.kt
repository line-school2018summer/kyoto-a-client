package intern.line.me.kyotoaclient.lib

import intern.line.me.kyotoaclient.lib.api.MessageDelete
import intern.line.me.kyotoaclient.lib.api.MessageUpdate
import java.sql.Timestamp

class Message(
    var id: Long,
    var room_id: Long,
    var user_id: Long,
    var text: String,
    var created_at: Timestamp,
    var updated_at: Timestamp
) {
    fun update(message: Message) {
        MessageUpdate(this, message).start()
    }

    fun delete() {
        MessageDelete(this).start()
    }
}

class MessageList(private val messages: MutableList<Message>) {
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

    private fun updateCount() {
        this.count = this.messages.count()
    }
}
