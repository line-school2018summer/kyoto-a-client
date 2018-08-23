package intern.line.me.kyotoaclient

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_change_my_profile.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.schedulers.Schedulers

class ChangeMyProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_my_profile)

        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        val retrofit = Retrofit.Builder().baseUrl("http://localhost:8080")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        val usersApi = retrofit.create(UsersApi::class.java)

        //val nonUidUser = NonUidUser(1, "kotlin")

        val regex = Regex("[[ぁ-んァ-ヶ亜-熙] \\w ー 。 、]+")

        val showName = findViewById<TextView>(R.id.show_name)
        val showId = findViewById<TextView>(R.id.show_id)
        val showCreatedAt = findViewById<TextView>(R.id.item_created_at)
        val showUpdatedAt = findViewById<TextView>(R.id.item_updated_at)

        usersApi.getMyInfo("token").subscribeOn(Schedulers.io()).subscribe{
            val nonUidUser = it
            showName.text = nonUidUser.name
            showId.text = nonUidUser.id.toString()
            showCreatedAt.text = nonUidUser.createdAt.toString()
            showUpdatedAt.text = nonUidUser.updatedAt.toString()
        }

        val changedName = findViewById<EditText>(R.id.changed_name)

        val button = findViewById<Button>(R.id.apply_button)
        button.setOnClickListener{
            val inputText = changedName.text.toString()
            var isValid = regex.matches(inputText)
            if(isValid){
                usersApi.changeUserInfo("token", inputText).subscribeOn(Schedulers.io()).subscribe{
                    show_name.text = it.name
                    show_updated_at.text = it.updatedAt.toString()
                }
            }
            else{
                changedName.error = "不正な文字が使われています"
            }
        }
    }
}
