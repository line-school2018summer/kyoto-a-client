package intern.line.me.kyotoaclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.content.Context
import android.widget.TextView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.lib.Message
import intern.line.me.kyotoaclient.lib.MessageList
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

class MessageListAdapter(private val context: Context): BaseAdapter() {
    var layoutInflater: LayoutInflater
    private var messages: MessageList? = null

    init {
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun setMessages(messages: MessageList) {
        this.messages = messages
    }

    fun getMessages(): MessageList? {
        return this.messages
    }

    override fun getCount(): Int {
        return messages?.count ?: 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = layoutInflater.inflate(R.layout.message_ballon, parent, false)
        val message: Message = messages?.messageAt(position) ?: throw Exception("message not found")
        val created_at: Timestamp = (message.created_at)
        val format = SimpleDateFormat("HH:mm")
        val time = Date(created_at.time)

        (convertView.findViewById(R.id.message_author) as TextView).setText(message.user.name)
        (convertView.findViewById(R.id.message_text) as TextView).setText((message.text))
        (convertView.findViewById(R.id.message_time) as TextView).setText(format.format(time))
        if (message.created_at != message.updated_at){
            (convertView.findViewById(R.id.message_modified) as TextView).visibility = View.VISIBLE
        }

        return convertView
    }

    override fun getItem(position: Int): Message {
        return messages?.messageAt(position) ?: throw Exception("message not found")
    }

    override fun getItemId(position: Int): Long {
        return messages?.messageAt(position)?.id ?: throw Exception("message not found")
    }
}