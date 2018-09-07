package intern.line.me.kyotoaclient.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.adapter.UserSelectListAdapter
import intern.line.me.kyotoaclient.model.entity.User
import intern.line.me.kyotoaclient.presenter.room.CreateRoom
import intern.line.me.kyotoaclient.presenter.user.GetMyInfo
import intern.line.me.kyotoaclient.presenter.user.GetUserList
import kotlinx.android.synthetic.main.activity_room_create.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class RoomCreateActivity : AppCompatActivity() {

    lateinit var adapter: UserSelectListAdapter
    lateinit var me: User

    private val job = Job()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_create)

        //アダプターの設定
        adapter = UserSelectListAdapter(this)
        user_select_list.adapter = adapter
        registerForContextMenu(user_select_list)

        //先にDBから取得
        launch(job + UI) {
            //TODO(ここボトルネック自分の情報をローカルに取りに行きたい)
            me = GetMyInfo().getMyInfo()

            val users = GetUserList().getUsersListFromDb()

            val filteredUserList = mutableListOf<User>()
            //自分を除外する
            users.forEach {
                if (it.id != me.id) {
                    filteredUserList.add(it)
                }
            }
            adapter.setUsers(filteredUserList)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    fun onCreateRoom(v: View) {
        println("on click!")
        val roomName = room_name_text.text.toString()
        var selectedUsers = adapter.getCheckedUserList()

        selectedUsers = (selectedUsers as MutableList<User>)
        selectedUsers.add(me)

        println(selectedUsers)

        launch (this.job + UI) {
            CreateRoom(roomName, selectedUsers).createRoom()
            goBack()
        }
    }

    fun goBack() {
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
    }
}