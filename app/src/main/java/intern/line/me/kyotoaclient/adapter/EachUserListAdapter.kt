package intern.line.me.kyotoaclient.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import intern.line.me.kyotoaclient.NonUidUser
import intern.line.me.kyotoaclient.R

class EachUserListAdapter(private val context: Context,
                      private val nonUidUserList: Array<NonUidUser>): BaseAdapter() {
    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.user_list_row, parent, false)
        val content = ViewHolder(view)
        val name = nonUidUserList[position].name
        content.name.text = name
        content.id.text = "id: ${getItemId(position).toString()}"
        content.icon.text = name.substring(0,1)
        return view
    }

    override fun getItem(position: Int): NonUidUser {
        return nonUidUserList[position]
    }

    override fun getItemId(position: Int): Long {
        return nonUidUserList[position].id
    }

    override fun getCount(): Int {
        return nonUidUserList.count()
    }

}

private class ViewHolder(view: View){
    val name = view.findViewById<TextView>(R.id.name_text)
    val id = view.findViewById<TextView>(R.id.id_text)
    val icon = view.findViewById<TextView>(R.id.name_icon)
}