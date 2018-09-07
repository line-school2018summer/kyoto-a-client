package intern.line.me.kyotoaclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.content.Context
import intern.line.me.kyotoaclient.R
import android.widget.CheckedTextView
import intern.line.me.kyotoaclient.model.entity.UserRealm

class UserSelectListAdapter(private val context: Context): BaseAdapter() {
    var layoutInflater: LayoutInflater

    private var users: List<UserRealm> = emptyList()
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
        checkView.text = users[position].name

        checkView.isChecked = checkList[position]
        checkView.setOnClickListener {
            val view = it as CheckedTextView
            view.toggle()
            checkList[position] = view.isChecked
        }
        return convertView
    }

    override fun getItem(position: Int): UserRealm {
        return users[position]
    }

    override fun getItemId(position: Int): Long {
        return users[position].id
    }


    fun setUsers(set_users: List<UserRealm>){
        users = set_users
        // 初期化
        checkList = MutableList(count, { false })
        notifyDataSetChanged()
    }

    fun getCheckedUserList(): List<UserRealm> {
        if (users.count() != checkList.count()) {
            throw Exception("invailed data")
        }
        val selectedUsers = mutableListOf<UserRealm>()
        for (i in 0..(users.count() - 1)){
            if (checkList[i]){
                selectedUsers.add(users[i])
            }
        }
        return selectedUsers
    }
}
