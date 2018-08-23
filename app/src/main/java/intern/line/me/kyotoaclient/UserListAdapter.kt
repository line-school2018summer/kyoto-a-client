package intern.line.me.kyotoaclient

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.FieldPosition

class UserListAdapter(private val context: Context,
                      private val nonUidUserList: Array<NonUidUser>): BaseAdapter() {
    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.user_list_row, parent, false)
        val content = ViewHolder(view)
        content.name.text = nonUidUserList[position].name
        content.id.text = getItemId(position).toString()
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
}