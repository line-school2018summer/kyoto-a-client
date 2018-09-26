package intern.line.me.kyotoaclient.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.adapter.UserSelectListAdapter
import intern.line.me.kyotoaclient.lib.util.FileUtils
import intern.line.me.kyotoaclient.model.entity.User
import intern.line.me.kyotoaclient.presenter.room.CreateRoom
import intern.line.me.kyotoaclient.presenter.room.PostRoomIcon
import intern.line.me.kyotoaclient.presenter.user.GetMyInfo
import intern.line.me.kyotoaclient.presenter.user.GetUserList
import kotlinx.android.synthetic.main.activity_room_create.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.*

class RoomCreateActivity : AppCompatActivity() {

    private val CHOSE_FILE_CODE: Int = 777
    var file: File? = null

    lateinit var adapter: UserSelectListAdapter
    lateinit var me: User

    private val job = Job()
    private val presenter = GetUserList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_create)

        //アダプターの設定
        registerForContextMenu(user_select_list)

        //先にDBから取得
        launch(job + UI) {
            //TODO(ここボトルネック自分の情報をローカルに取りに行きたい)
            me = GetMyInfo().getMyInfo()
            adapter = UserSelectListAdapter(applicationContext,presenter.getUsersListExcludeId(me.id))
            user_select_list.adapter = adapter
            presenter.getUsersList()
        }

        create_room_icon_view.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, CHOSE_FILE_CODE)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try{
            if(requestCode == CHOSE_FILE_CODE && resultCode == RESULT_OK && data!=null){
                val uri = Uri.parse(data.dataString)
                file = FileUtils(this).getFile(uri, "new_room_icon_" + Random().nextInt(100).toString()) ?: throw Exception("no file found")
                val file = file ?: throw Exception("invalid file")

                val image = BitmapFactory.decodeStream(file.inputStream())
                create_room_icon_view.setImageBitmap(image)
            }
        } catch(t: UnsupportedEncodingException) {
            Toast.makeText(this, "not supported", Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onCreateRoom(v: View) {
        val roomName = room_name_text.text.toString()
        var selectedUsers = adapter.getCheckedUserList()

        selectedUsers = (selectedUsers as MutableList<User>)
        selectedUsers.add(me)

        launch (this.job + UI) {
            val room = CreateRoom(roomName, selectedUsers).createRoom()

            if(file != null) {
                PostRoomIcon().postRoomIcon(room.id, file!!)
                file!!.delete()
            }

            goBack()
        }
    }

    private fun goBack() {
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
    }
}