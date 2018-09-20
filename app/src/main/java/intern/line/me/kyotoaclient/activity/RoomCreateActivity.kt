package intern.line.me.kyotoaclient.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.adapter.UserSelectListAdapter
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
        }

        create_room_icon_view.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("file/*")
            startActivityForResult(intent, CHOSE_FILE_CODE)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val context = this

        try{
            if(requestCode == CHOSE_FILE_CODE && resultCode == RESULT_OK && data!=null){
                var filePath = data.getDataString()
                filePath=filePath.substring(filePath.indexOf("storage"))
                val decodedPath = URLDecoder.decode(filePath, "utf-8")
                //val decodedPath = "/sdcard/P.jpg"
                Toast.makeText(this, decodedPath, Toast.LENGTH_LONG).show()

                //TODO(file選択方法)
                file =  File(decodedPath)
                if(file != null) {
                    val image = BitmapFactory.decodeStream(file!!.inputStream())
                    create_room_icon_view.setImageBitmap(image)
                }
            }
        } catch(t: UnsupportedEncodingException) {
            Toast.makeText(this, "not supported", Toast.LENGTH_SHORT).show()
        }
    }

    fun onCreateRoom(v: View) {
        println("on click!")
        val roomName = room_name_text.text.toString()
        var selectedUsers = adapter.getCheckedUserList()

        selectedUsers = (selectedUsers as MutableList<User>)
        selectedUsers.add(me)

        println(selectedUsers)

        launch (this.job + UI) {
            val room = CreateRoom(roomName, selectedUsers).createRoom()

            if(file != null) {
                PostRoomIcon().postRoomIcon(room.id, file!!)
            }

            goBack()
        }
    }

    fun goBack() {
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
    }
}