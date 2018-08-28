package intern.line.me.kyotoaclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.content.Context
import android.text.format.DateUtils
import android.text.format.DateUtils.*
import android.widget.TextView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.lib.model.Message
import intern.line.me.kyotoaclient.lib.model.MessageList
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
        return messages?.count?.toInt() ?: 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view = convertView ?: layoutInflater.inflate(R.layout.message_ballon, parent, false)
        val message: Message = messages?.messageAt(position) ?: throw Exception("message not found")
        val oldMessage: Message? = messages?.messageAt(position - 1)
        val oldTime = oldMessage?.created_at?.time
        var oldYear: String? = null
        var oldDate: String? = null
        val created_at: Timestamp = (message.created_at)
        val format = SimpleDateFormat("HH:mm")
        val formatForDateChk = SimpleDateFormat("MM/dd")
        val formatForYearChk = SimpleDateFormat("yyyy")
        if (oldTime != null) {
            oldYear = formatForYearChk.format(Date(oldTime))
            oldDate = formatForDateChk.format(Date(oldTime))
        }
        val time = Date(created_at.time)
        val newDate = formatForDateChk.format(time)
        val newYear = formatForYearChk.format(time)
        var flags = 0
        if (oldYear != newYear) {
            flags = flags or FORMAT_SHOW_YEAR or FORMAT_SHOW_DATE
        } else if (oldDate != newDate) {
            // 年が違っても日付が同じことがある
            flags = flags or FORMAT_SHOW_DATE
        }
        val dateView = view.findViewById(R.id.message_date) as TextView
        if (flags != 0){
            dateView.setText(DateUtils.formatDateTime(context, created_at.time,  flags or FORMAT_ABBREV_ALL))
            dateView.visibility = View.VISIBLE
        } else {
            dateView.visibility = View.GONE
        }

        (view.findViewById(R.id.message_author) as TextView).setText(message.user.name)
        (view.findViewById(R.id.message_text) as TextView).setText((message.text))
        (view.findViewById(R.id.message_time) as TextView).setText(format.format(time))
        if (message.created_at != message.updated_at){
            (view.findViewById(R.id.message_modified) as TextView).visibility = View.VISIBLE
        } else {
            (view.findViewById(R.id.message_modified) as TextView).visibility = View.GONE
        }

        return view
    }

    override fun getItem(position: Int): Message {
        return messages?.messageAt(position) ?: throw Exception("message not found")
    }

    override fun getItemId(position: Int): Long {
        return messages?.messageAt(position)?.id ?: throw Exception("message not found")
    }
}