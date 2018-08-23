package intern.line.me.kyotoaclient

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.schedulers.Schedulers
import java.sql.Timestamp

class GetUserProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_user_profile)

        val selectedId = intent.getLongExtra("longTapUserId", 1)

        val showName = findViewById<TextView>(R.id.show_name)
        val showId = findViewById<TextView>(R.id.show_id)
        val showCreatedAt = findViewById<TextView>(R.id.item_created_at)

        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        val retrofit = Retrofit.Builder().baseUrl("http://localhost:8080")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        val usersApi = retrofit.create(UsersApi::class.java)

        usersApi.getUserInfoById(selectedId).subscribeOn(Schedulers.io()).subscribe{
            val nonUidUser = it
            showName.text = nonUidUser.name
            showId.text = nonUidUser.id.toString()
            showCreatedAt.text = nonUidUser.createdAt.toString()
        }

        /*
        val timestamp = Timestamp(System.currentTimeMillis())
        val nonUidUser = NonUidUser(1, "kot", timestamp, timestamp)
        showName.text = nonUidUser.name
        showId.text = nonUidUser.id.toString()
        */
    }
}
