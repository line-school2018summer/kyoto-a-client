package intern.line.me.kyotoaclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.content.Context
import android.widget.TextView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.lib.Room
import intern.line.me.kyotoaclient.lib.RoomList
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

class RoomListAdapter(private val context: Context): BaseAdapter() {
    var layoutInflater: LayoutInflater

    private var rooms: RoomList? = null

    init {
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun setRooms(rooms: RoomList) {
        this.rooms = rooms
    }

    override fun getCount(): Int {
        return rooms?.count ?: 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = layoutInflater.inflate(R.layout.room_display, parent, false)
        val created_at: Timestamp = (rooms?.roomAt(position)?.messageTime ?: throw Exception("room not found"))
        val format = SimpleDateFormat("HH:mm")
        val time = Date(created_at.time)
        (convertView.findViewById(R.id.room_name_view) as TextView).setText((rooms?.roomAt(position)?.name ?: throw Exception("room not found")))
        (convertView.findViewById(R.id.latest_message_view) as TextView).setText((rooms?.roomAt(position)?.latestMessage ?: throw Exception("room not found")))
        (convertView.findViewById(R.id.message_time_view) as TextView).setText(format.format(time))
        return convertView
    }

    override fun getItem(position: Int): Room {
        return rooms?.roomAt(position) ?: throw Exception("room not found")
    }

    override fun getItemId(position: Int): Long {
        return rooms?.roomAt(position)?.id ?: throw Exception("room not found")
    }
}