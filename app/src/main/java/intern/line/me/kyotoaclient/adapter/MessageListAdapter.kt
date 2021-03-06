package intern.line.me.kyotoaclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.text.format.DateUtils
import android.text.format.DateUtils.*
import android.widget.ListAdapter
import android.widget.TextView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.model.entity.Message
import io.realm.RealmBaseAdapter
import io.realm.RealmResults
import java.lang.Math.max
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

class MessageListAdapter(private val context: Context, private val realm_results : RealmResults<Message>): RealmBaseAdapter<Message>(realm_results), ListAdapter {
     private val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun getView(position: Int,
						 convertView: View?,
						 parent: ViewGroup?): View{
        val view = convertView ?: layoutInflater.inflate(R.layout.message_ballon, parent, false)

        val message: Message = adapterData!![position] //選択されたメッセージは必ず存在すると考える

		val oldMessage: Message? = adapterData!![max(0,position - 1)]
        val oldTime = oldMessage?.created_at?.time
        var oldYear: String? = null
        var oldDate: String? = null

        val createdAt: Timestamp = (Timestamp(message.created_at.time))
        val format = SimpleDateFormat("HH:mm")
        val formatForDateChk = SimpleDateFormat("MM/dd")
        val formatForYearChk = SimpleDateFormat("yyyy")
        if (oldTime != null) {
            oldYear = formatForYearChk.format(Date(oldTime))
            oldDate = formatForDateChk.format(Date(oldTime))
        }

        val time = Date(createdAt.time)
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
			dateView.text = DateUtils.formatDateTime(context, createdAt.time,  flags or FORMAT_ABBREV_ALL)
            dateView.visibility = View.VISIBLE
        } else {
            dateView.visibility = View.GONE
        }

        (view.findViewById(R.id.message_author) as TextView).text = message.user_name
        (view.findViewById(R.id.message_text) as TextView).text = (message.text)
        (view.findViewById(R.id.message_time) as TextView).text = format.format(time)

        if (message.created_at != message.updated_at){
            (view.findViewById(R.id.message_modified) as TextView).visibility = View.VISIBLE
        } else {
            (view.findViewById(R.id.message_modified) as TextView).visibility = View.GONE
        }

        return view
    }

	override fun getItemId(position: Int): Long {
		return super.getItem(position)!!.id
	}
}