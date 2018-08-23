package intern.line.me.kyotoaclient

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
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

        val presentName = findViewById<TextView>(R.id.present_name)
        usersApi.getMyInfo("token").subscribeOn(Schedulers.io()).subscribe{
            presentName.text = it.name
        }

        val changedName = findViewById<EditText>(R.id.changed_name)

        val button = findViewById<Button>(R.id.apply_button)
        button.setOnClickListener{
            val inputText = changedName.text.toString()
            var isValid = regex.matches(inputText)
            if(isValid){
                usersApi.changeUserInfo("token", inputText).subscribeOn(Schedulers.io()).subscribe{
                    presentName.text = it.name
                }
            }
            else{
                changedName.error = "不正な文字が使われています"
            }
        }
    }
}
