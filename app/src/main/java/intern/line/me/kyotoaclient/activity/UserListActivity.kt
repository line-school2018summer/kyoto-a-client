package intern.line.me.kyotoaclient.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.adapter.UserListAdapter
import intern.line.me.kyotoaclient.presenter.GetUserList

class UserListActivity : AppCompatActivity() {

    lateinit var adapter: UserListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        setResult(Activity.RESULT_CANCELED)

        val list = findViewById<ListView>(R.id.user_list)
        val button = findViewById<Button>(R.id.profile_button)

        adapter = UserListAdapter(this)
        list.adapter = adapter

        //非同期でユーザー取得
        GetUserList {
            adapter.setUsers(it)
        }.start()


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
    }

    override fun onRestart() {
        super.onRestart()
        
        //非同期でユーザー取得
        GetUserList {
            adapter.setUsers(it)
        }.start()
    }
}
