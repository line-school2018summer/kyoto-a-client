package intern.line.me.kyotoaclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.content.Context
import android.widget.TextView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.lib.User
import intern.line.me.kyotoaclient.lib.UserList
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

class UserListAdapter(private val context: Context): BaseAdapter() {
    var layoutInflater: LayoutInflater

    private var users: List<User> = emptyList()

    init {
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun setUsers(users: UserList) {
        this.users = users
    }

    override fun getCount(): Int {
        return users?.count ?: 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = layoutInflater.inflate(R.layout.user_select, parent, false)
        (convertView.findViewById(R.id.user_name_view) as TextView).setText((users?.userAt(position)?.name ?: throw Exception("user not found")))
        return convertView
    }

    override fun getItem(position: Int): User {
        return users?.userAt(position) ?: throw Exception("user not found")
    }

    override fun getItemId(position: Int): Long {
        return users?.userAt(position)?.id ?: throw Exception("user not found")
    }


    fun setUsers(set_users: List<User>){
        users = set_users
    }
}