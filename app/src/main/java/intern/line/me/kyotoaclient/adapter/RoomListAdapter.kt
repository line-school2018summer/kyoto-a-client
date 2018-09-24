package intern.line.me.kyotoaclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.model.entity.Room
import intern.line.me.kyotoaclient.presenter.room.GetRoomIcon
import io.realm.RealmBaseAdapter
import io.realm.RealmResults
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable

class RoomListAdapter(private val context: Context, realm_results: RealmResults<Room>) : RealmBaseAdapter<Room>(realm_results), ListAdapter {
    var layoutInflater: LayoutInflater

    init {
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView =convertView?:  layoutInflater.inflate(R.layout.room_display, parent, false)
        val format = SimpleDateFormat("HH:mm")
        val lastMessage_text = adapterData!![position].last_message_text
        val id = getItemId(position)
        if (lastMessage_text != null) {

            val created_at: Timestamp = Timestamp(adapterData!![position].last_message_created_at!!.time)
            val time = Date(created_at.time)

            (convertView.findViewById(R.id.message_time_view) as TextView).text = format.format(time)
            (convertView.findViewById(R.id.latest_message_view) as TextView).text = lastMessage_text
        }
        (convertView.findViewById(R.id.room_name_view) as TextView).text = adapterData!![position].name

        val imageView = (convertView.findViewById(R.id.room_icon_view) as ImageView)
        //ビューが使いまわされるので一度nullにする
        imageView.setImageDrawable(null)

        val dirName = "room"
        val dir = File(context.filesDir, dirName)
        if (!dir.exists()) {
            dir.mkdir()
        }
        launch(UI) {
            val roomId = getItemId(position)
            val fileName = "room/icon_id_" + roomId.toString()
            val file = File(context.filesDir, fileName)
            if (!file.exists()) {
                GetRoomIcon().getRoomIcon(roomId).let {
                    val image = BitmapFactory.decodeStream(it)
                    val fos = FileOutputStream(file)
                    image.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.close()
                    imageView.setImageBitmap(image)
                }
            } else {
                val fis = FileInputStream(file)
                val image = BitmapFactory.decodeStream(fis)
                if (image != null) {
                    imageView.setImageBitmap(image)
                }
            }
        }

        return convertView
    }


    override fun getItemId(position: Int): Long {
        return super.getItem(position)?.id ?: 0
    }
}
