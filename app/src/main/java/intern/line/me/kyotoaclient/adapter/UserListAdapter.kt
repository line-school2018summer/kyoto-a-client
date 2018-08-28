package intern.line.me.kyotoaclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.content.Context
import android.widget.TextView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.lib.model.User
import intern.line.me.kyotoaclient.lib.model.UserList

class UserListAdapter(private val context: Context): BaseAdapter() {
    var layoutInflater: LayoutInflater

    private var users: List<User> = emptyList()

    init {
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


    override fun getCount(): Int {
        return users.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = layoutInflater.inflate(R.layout.user_list_row, parent, false)
        (convertView.findViewById(R.id.name_text) as TextView).setText(users[position].name)
        return convertView
    }

    override fun getItem(position: Int): User {
        return users[position]
    }

    override fun getItemId(position: Int): Long {
        return users[position].id
    }


    fun setUsers(set_users: List<User>){
        users = set_users
        notifyDataSetChanged()
    }
}
