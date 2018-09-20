package intern.line.me.kyotoaclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import com.bumptech.glide.Glide
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.model.entity.User
import intern.line.me.kyotoaclient.presenter.user.GetIcon
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter
import kotlinx.android.synthetic.main.activity_get_user_profile.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

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
            val imageVIew = (convertView.findViewById(R.id.icon) as ImageView)
            launch( UI ){
                GetIcon(getItemId(position)).getIcon().let {
                    val image = BitmapFactory.decodeStream(it)
                    imageVIew.setImageBitmap(image)
                }
            }
        }
        return convertView
    }
}