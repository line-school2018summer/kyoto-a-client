package intern.line.me.kyotoaclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.content.Context
import android.widget.TextView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.model.User
import android.widget.CheckedTextView

class UserSelectListAdapter(private val context: Context): BaseAdapter() {
    var layoutInflater: LayoutInflater

    private var users: List<User> = emptyList()
    private var checkList = mutableListOf<Boolean>()

    init {
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


    override fun getCount(): Int {
        return users.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = layoutInflater.inflate(R.layout.user_select, parent, false)
        val checkView: CheckedTextView = convertView.findViewById(R.id.user_name_view)
        checkView.setText(users[position].name)
        checkView.isChecked = checkList[position]
        checkView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val view = v as CheckedTextView?
                view ?: return
                view.toggle()
                checkList[position] = view.isChecked
            }
        })
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
        // 初期化
        checkList = MutableList(count, { false })
        notifyDataSetChanged()
    }

    fun getCheckedUserList(): List<User> {
        if (users.count() != checkList.count()) {
            throw Exception("invailed data")
        }
        val selectedUsers = mutableListOf<User>()
        for (i in 0..(users.count() - 1)){
            if (checkList[i]){
                selectedUsers.add(users[i])
            }
        }
        return selectedUsers
    }
}
