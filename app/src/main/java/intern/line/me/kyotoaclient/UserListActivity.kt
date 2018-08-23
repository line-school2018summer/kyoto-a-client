package intern.line.me.kyotoaclient

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.schedulers.Schedulers

class UserListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?): Boolean {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        setResult(Activity.RESULT_CANCELED)

        val list = findViewById<ListView>(R.id.user_list)

        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        val retrofit = Retrofit.Builder().baseUrl("http://localhost:8080")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        val usersApi = retrofit.create(UsersApi::class.java)

        lateinit var adapter: UserListAdapter

        usersApi.getUsers().subscribeOn(Schedulers.io()).subscribe{
            val nonUidUserList = it.toTypedArray()
            adapter = UserListAdapter(this, nonUidUserList)
            list.adapter = adapter
        }

        //val nonUidUserList = arrayOf(NonUidUser(1, "kotlin"))


        list.setOnItemClickListener{_, _, position, _ ->
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
            return@setOnItemLongClickListener false
        }
    }
}
