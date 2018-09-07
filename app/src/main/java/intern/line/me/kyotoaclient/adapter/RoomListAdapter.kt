package intern.line.me.kyotoaclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.content.Context
import android.widget.TextView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.model.entity.MessageRealm
import intern.line.me.kyotoaclient.model.entity.RoomRealm
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

class RoomListAdapter(private val context: Context): BaseAdapter() {
    var layoutInflater: LayoutInflater

    private var rooms: List<RoomRealm> = emptyList()

    init {
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun setRooms(set_rooms: List<RoomRealm>){
        rooms = set_rooms
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return rooms.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = layoutInflater.inflate(R.layout.room_display, parent, false)
        val format = SimpleDateFormat("HH:mm")
        val lastMessage: MessageRealm? = rooms[position].last_message
        if (lastMessage != null) {
            val created_at: Timestamp = Timestamp(lastMessage.created_at.time)
            val time = Date(created_at.time)
            (convertView.findViewById(R.id.message_time_view) as TextView).setText(format.format(time))
            (convertView.findViewById(R.id.latest_message_view) as TextView).setText((lastMessage.text))
        }
        (convertView.findViewById(R.id.room_name_view) as TextView).setText((rooms[position].name ?: throw Exception("room not found")))
        return convertView
    }

    override fun getItem(position: Int): RoomRealm {
        return rooms[position] ?: throw Exception("room not found")
    }

    override fun getItemId(position: Int): Long {
        return rooms[position].id ?: throw Exception("room not found")
    }
}