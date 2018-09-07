package intern.line.me.kyotoaclient.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.adapter.UserListAdapter
import intern.line.me.kyotoaclient.presenter.user.SearchUsers
import intern.line.me.kyotoaclient.presenter.user.GetUserList
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class UserListActivity : AppCompatActivity() {

    lateinit var adapter: UserListAdapter

    //非同期処理管理用
    private val job = Job()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        setResult(Activity.RESULT_CANCELED)

        val list = findViewById<ListView>(R.id.user_list)
        val button = findViewById<Button>(R.id.profile_button)
        val searchButton = findViewById<Button>(R.id.top)
        val searchBox = findViewById<EditText>(R.id.search_box)

        adapter = UserListAdapter(this)
        list.adapter = adapter

        //非同期でユーザー取得
        updateUserList()


        button.setOnClickListener {
            val intent = Intent(this, ChangeMyProfileActivity::class.java)
            startActivity(intent)
        }

        list.setOnItemClickListener { _, _, position, _ ->
            val selectedUserId = adapter.getItemId(position)
            val result = Intent()
            result.putExtra("selectedUserId", selectedUserId)
            setResult(Activity.RESULT_OK, result)
            finish()
        }


        list.setOnItemLongClickListener { _, _, position, _ ->
            val longTapUserId = adapter.getItemId(position)
            val intent = Intent(this, GetUserProfileActivity::class.java)
            intent.putExtra("longTapUserId", longTapUserId)
            startActivityForResult(intent, 11)
            return@setOnItemLongClickListener true
        }

        searchButton.setOnClickListener{
            val name = searchBox.text.toString()
            launch(job + UI) {
                SearchUsers(name).getUsersList().let {
                adapter.setUsers(it)
                }
            }.start()
        }
    }

    override fun onRestart() {
        super.onRestart()

        updateUserList()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    //非同期でユーザー取得
    fun updateUserList(){
        launch(job + UI) {
            val cli = GetUserList()

            cli.getUsersListFromDb().let {
                adapter.setUsers(it)
            }

            GetUserList().getUsersList().let{
                adapter.setUsers(it)
            }
        }
    }
}
