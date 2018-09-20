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
    lateinit var list: ListView

    //非同期処理管理用
    private val job = Job()

    private  val presenter = GetUserList()
    private val search_presenter = SearchUsers()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        setResult(Activity.RESULT_CANCELED)

         list = findViewById<ListView>(R.id.user_list)
        val button = findViewById<Button>(R.id.profile_button)
        val searchButton = findViewById<Button>(R.id.top)
        val searchBox = findViewById<EditText>(R.id.search_box)

        //これで自動的にDBの更新がアダプターに反映される
        adapter = UserListAdapter(this, presenter.getUsersListFromDb())
        list.adapter = adapter

        //APIを叩いてローカルDBを更新
        launch(job + UI) {
            presenter.getUsersList()
        }

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

        searchButton.setOnClickListener {
            val name = searchBox.text.toString()
            adapter = UserListAdapter(this, search_presenter.getUsersListFromDB(name))
            list.adapter = adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}
