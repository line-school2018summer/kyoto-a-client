package intern.line.me.kyotoaclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.content.Context
import android.widget.ListAdapter
import android.widget.TextView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.model.entity.Message
import intern.line.me.kyotoaclient.model.entity.Room
import intern.line.me.kyotoaclient.model.entity.User
import io.realm.RealmBaseAdapter
import io.realm.RealmResults
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

class RoomListAdapter(private val context: Context, realm_results: RealmResults<Room>) : RealmBaseAdapter<Room>(realm_results), ListAdapter {
    var layoutInflater: LayoutInflater

    init {
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = layoutInflater.inflate(R.layout.room_display, parent, false)
        val format = SimpleDateFormat("HH:mm")
        val lastMessage: Message? = adapterData!![position].last_message
        if (lastMessage != null) {
            val created_at: Timestamp = Timestamp(lastMessage.created_at.time)
            val time = Date(created_at.time)
            (convertView.findViewById(R.id.message_time_view) as TextView).text = format.format(time)
            (convertView.findViewById(R.id.latest_message_view) as TextView).text = lastMessage.text
        }
        (convertView.findViewById(R.id.room_name_view) as TextView).text = adapterData!![position].name
        return convertView
    }


    override fun getItemId(position: Int): Long {
        return super.getItem(position)?.id ?: 0
    }
}