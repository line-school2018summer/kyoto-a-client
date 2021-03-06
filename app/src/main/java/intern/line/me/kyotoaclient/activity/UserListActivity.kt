package intern.line.me.kyotoaclient.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.adapter.UserListAdapter
import intern.line.me.kyotoaclient.presenter.user.SearchUsers
import intern.line.me.kyotoaclient.presenter.user.GetUserList
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class UserListActivity : AppCompatActivity() {

    lateinit var adapter: UserListAdapter
    lateinit var list: ListView

    //非同期処理管理用
    private val job = Job()

    private  val presenter = GetUserList()
    private val searchPresenter = SearchUsers()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        setResult(Activity.RESULT_CANCELED)

        list = findViewById(R.id.user_list)
        val button = findViewById<FloatingActionButton>(R.id.settings)
        val searchButton = findViewById<ImageButton>(R.id.search_button)
      
        //画像キャッシュリセット
        UserListAdapter.list.clear()
      
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
            val longTapUserId = adapter.getItemId(position)
            val intent = Intent(this, GetUserProfileActivity::class.java)
            intent.putExtra("longTapUserId", longTapUserId)
            startActivityForResult(intent, 11)
        }


        searchButton.setOnClickListener {
            val name = searchBox.text.toString()
            adapter = UserListAdapter(this, searchPresenter.getUsersListFromDB(name))
            list.adapter = adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}
