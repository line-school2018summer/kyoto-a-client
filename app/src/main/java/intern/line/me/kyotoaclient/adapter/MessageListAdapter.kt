package intern.line.me.kyotoaclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.content.Context
import android.widget.TextView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.lib.Message
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

class MessageListAdapter(private val context: Context): BaseAdapter() {
    var layoutInflater: LayoutInflater

    @JvmField
    var messages = mutableListOf<Message>()

    init {
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


    override fun getCount(): Int {
        return messages.count()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = layoutInflater.inflate(R.layout.message_ballon, parent, false)
        val message: Message = messages[position]
        val created_at: Timestamp = (message.created_at)
        val format = SimpleDateFormat("HH:mm")
        val time = Date(created_at.time)

        (convertView.findViewById(R.id.message_author) as TextView).text = message.user.name
        (convertView.findViewById(R.id.message_text) as TextView).text = message.text
        (convertView.findViewById(R.id.message_time) as TextView).text = format.format(time)

        if (message.created_at != message.updated_at){
            (convertView.findViewById(R.id.message_modified) as TextView).visibility = View.VISIBLE
        }


        return convertView
    }

    override fun getItem(position: Int): Message? {
        return messages[position]
    }

    override fun getItemId(position: Int): Long {
        return messages[position].id
    }


    fun setMessages(set_messages: MutableList<Message>){
        messages = set_messages
        notifyDataSetChanged()
    }

    fun addMessages(add_messages: List<Message>){
        messages.addAll(add_messages)
        notifyDataSetChanged()
    }

    fun updateMessage(position: Int, updated_message: Message){
        messages[position] = updated_message
        notifyDataSetChanged()
    }

    fun deleteMessage(position: Int){
        messages.removeAt(position)
        notifyDataSetChanged()
    }


}