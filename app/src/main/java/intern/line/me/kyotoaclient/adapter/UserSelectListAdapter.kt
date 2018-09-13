package intern.line.me.kyotoaclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.content.Context
import intern.line.me.kyotoaclient.R
import android.widget.CheckedTextView
import android.widget.ListAdapter
import intern.line.me.kyotoaclient.model.entity.User
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter

class UserSelectListAdapter(private val context: Context,private val realm_results : OrderedRealmCollection<User>): RealmBaseAdapter<User>(realm_results), ListAdapter{
    var layoutInflater: LayoutInflater

    var  checkList : MutableList<Boolean>

    init {
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this. checkList = MutableList<Boolean>(count,{false})
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView?: layoutInflater.inflate(R.layout.user_select, parent, false)
        val checkView: CheckedTextView = convertView.findViewById(R.id.user_name_view)
        checkView.text = adapterData!![position].name

        checkView.setOnClickListener {
            val view = it as CheckedTextView
            view.toggle()
            checkList[position] = view.isChecked
        }
        checkView.isChecked = checkList[position]

        return convertView
    }


    override fun getItemId(position: Int): Long {
        return super.getItem(position)?.id ?: 0
    }


    fun getCheckedUserList(): List<User> {
        if (adapterData!!.count() != checkList.count()) {
            throw Exception("invailed data")
        }
        val selectedUsers = mutableListOf<User>()
        for (i in 0..(adapterData!!.count() - 1)){
            if (checkList[i]){
                selectedUsers.add(adapterData!![i])
            }
        }
        return selectedUsers
    }
}
