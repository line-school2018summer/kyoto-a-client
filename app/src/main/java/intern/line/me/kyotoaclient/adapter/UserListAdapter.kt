package intern.line.me.kyotoaclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.model.entity.User
import intern.line.me.kyotoaclient.presenter.user.GetIcon
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class UserListAdapter(private val context: Context, private val realm_results : OrderedRealmCollection<User>) : RealmBaseAdapter<User>(realm_results), ListAdapter {
    private val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    companion object {
        val list = mutableMapOf<Long, Bitmap>()
    }

    override fun getItemId(position: Int): Long {
        return super.getItem(position)?.id ?: 0
    }

    override fun getView(position: Int, originalConvertView: View?, parent: ViewGroup?): View? {
        val convertView = originalConvertView
                ?: layoutInflater.inflate(R.layout.user_list_row, parent, false)
        if (adapterData != null) {
            val user = adapterData!![position]
            (convertView.findViewById(R.id.name_text) as TextView).text = user.name
            val imageView = (convertView.findViewById(R.id.icon) as ImageView)
            val id = user.id
            //ビューが使いまわされるので一度nullにする
            imageView.setImageDrawable(null)


            launch(UI) {
                if (list[id] == null) {
                    GetIcon(id).getIcon().let {
                        val image = BitmapFactory.decodeStream(it)
                        list[id] = image
                    }
                }
                imageView.setImageBitmap(list[id])
            }
        }
        return convertView
    }
}