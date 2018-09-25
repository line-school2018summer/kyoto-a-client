package intern.line.me.kyotoaclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import intern.line.me.kyotoaclient.R
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.ListAdapter
import intern.line.me.kyotoaclient.model.entity.User
import intern.line.me.kyotoaclient.presenter.user.GetIcon
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class UserSelectListAdapter(private val context: Context,private val realm_results : OrderedRealmCollection<User>): RealmBaseAdapter<User>(realm_results), ListAdapter{
    var layoutInflater: LayoutInflater

    var  checkList : MutableList<Boolean>

	companion object {
		val list = mutableMapOf<Long, Bitmap>()
	}

    init {
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this. checkList = MutableList(count,{false})
    }

    override fun getView(position: Int, originalConvertView: View?, parent: ViewGroup?): View? {
        checkList.addAll(MutableList(count-checkList.size,{false}))
        val convertView = originalConvertView?: layoutInflater.inflate(R.layout.user_select, parent, false)
        val checkView: CheckedTextView = convertView.findViewById(R.id.user_name_view)
        checkView.text = adapterData!![position].name

        checkView.setOnClickListener {
            val view = it as CheckedTextView
            view.toggle()
            checkList[position] = view.isChecked
        }
        checkView.isChecked = checkList[position]

		val user = adapterData!![position]
		val id = user.id

        launch(UI) {
            if (UserSelectListAdapter.list[id] == null) {
                GetIcon(id).getIcon().let {
                    val image = BitmapFactory.decodeStream(it)
					UserSelectListAdapter.list[id] = image
                }
            }

			val imageView = (convertView.findViewById(R.id.user_select_icon) as ImageView)

			imageView.setImageBitmap(UserSelectListAdapter.list[id])
        }

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
