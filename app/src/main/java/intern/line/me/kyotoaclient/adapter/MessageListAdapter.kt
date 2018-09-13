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
import intern.line.me.kyotoaclient.model.entity.Message
import java.lang.Math.max
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

class MessageListAdapter(private val context: Context): BaseAdapter() {
    var layoutInflater: LayoutInflater

    var messages : MutableList<Message>? = null

    init {
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        if(messages != null) return messages!!.size

		return 0
    }

    override fun getView(position: Int,
						 convertView: View?,
						 parent: ViewGroup?): View{
        val view = convertView ?: layoutInflater.inflate(R.layout.message_ballon, parent, false)

        val message: Message = messages!![position] //選択されたメッセージは必ず存在すると考える

		val oldMessage: Message? = messages!![max(0,position - 1)]
        val oldTime = oldMessage?.created_at?.time
        var oldYear: String? = null
        var oldDate: String? = null

        val created_at: Timestamp = (Timestamp(message.created_at.time))
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
			dateView.text = DateUtils.formatDateTime(context, created_at.time,  flags or FORMAT_ABBREV_ALL)
            dateView.visibility = View.VISIBLE
        } else {
            dateView.visibility = View.GONE
        }

        (view.findViewById(R.id.message_author) as TextView).text = message.user?.name
        (view.findViewById(R.id.message_text) as TextView).text = (message.text)
        (view.findViewById(R.id.message_time) as TextView).text = format.format(time)

        if (message.created_at != message.updated_at){
            (view.findViewById(R.id.message_modified) as TextView).visibility = View.VISIBLE
        } else {
            (view.findViewById(R.id.message_modified) as TextView).visibility = View.GONE
        }

        return view
    }

    override fun getItem(position: Int): Message {
        return messages!![position]
    }

    override fun getItemId(position: Int): Long {
        return messages!![position]?.id
    }
}