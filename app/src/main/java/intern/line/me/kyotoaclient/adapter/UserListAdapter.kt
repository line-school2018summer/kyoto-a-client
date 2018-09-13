package intern.line.me.kyotoaclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.widget.ListAdapter
import android.widget.TextView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.model.entity.User
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter
class UserListAdapter(private val context: Context, private val realm_results : OrderedRealmCollection<User>) : RealmBaseAdapter<User>(realm_results), ListAdapter {
    var layoutInflater: LayoutInflater

    init {
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getItemId(position: Int): Long {
        return super.getItem(position)?.id ?: 0
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        var convertView = convertView
                ?: layoutInflater.inflate(R.layout.user_list_row, parent, false)
        if (adapterData != null) {
            val user = adapterData!![position]
            (convertView.findViewById(R.id.name_text) as TextView).text = user.name
            (convertView.findViewById(R.id.name_icon) as TextView).text = user.name.substring(0, 1)
        }
        return convertView
    }
}