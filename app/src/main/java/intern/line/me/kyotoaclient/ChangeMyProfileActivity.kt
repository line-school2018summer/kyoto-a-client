package intern.line.me.kyotoaclient

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import intern.line.me.kyotoaclient.lib.api.interfaces.UsersApi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.schedulers.Schedulers

class ChangeMyProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_my_profile)

        val token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjMxNjAwMjk1MjI3ODA5M2RmODA3YzkxMGNjYTBmODE3YmI4ODcxY2YifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vbGluZS1zdW1tZXIta3lvdG8tYSIsImF1ZCI6ImxpbmUtc3VtbWVyLWt5b3RvLWEiLCJhdXRoX3RpbWUiOjE1MzUwODkzNzYsInVzZXJfaWQiOiJYNG9SUFdKclFVYnZRd0ZMekY5bDk4cGN6ZGgxIiwic3ViIjoiWDRvUlBXSnJRVWJ2UXdGTHpGOWw5OHBjemRoMSIsImlhdCI6MTUzNTA4OTM3NiwiZXhwIjoxNTM1MDkyOTc2LCJlbWFpbCI6ImhvZ2VAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7ImVtYWlsIjpbImhvZ2VAZ21haWwuY29tIl19LCJzaWduX2luX3Byb3ZpZGVyIjoicGFzc3dvcmQifX0.WLyray3gDuvkLyULs7VAaJxD1b91FWpm8qN6FZwpnP69PaikXCIR78TPMaCRQTCpjXEFj8M5YkTf2KErGLVy6iQUZZkS4xP4mqjnpMUAyRLCv5a9KxDbtYzVmJU6zmeFx30XO2iRegBcu6Jga79SWSc6MGe5I7QfPMX25w_9K7qud2hRwVeg_K15ipexWPrrpdQ-NUJfPSOmF1yv71hZuhwVDGU7WdnscymK7VqR2w0nmZsiP6esX-wDIKlH6o5386pNsJ_7RG9JNzqRpvFPMwMaw3gGiPP9KDhJ16eG8GTlUIwx9-j4qaDz5Y79p32MUs129sBLbcziRqVnmTGNAg"


        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        val retrofit = Retrofit.Builder().baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        val usersApi = retrofit.create(UsersApi::class.java)


        val regex = Regex("[[ぁ-んァ-ヶ亜-熙] \\w ー 。 、]+")

        val showName = findViewById<TextView>(R.id.show_name)
        val showId = findViewById<TextView>(R.id.show_id)
        val showCreatedAt = findViewById<TextView>(R.id.item_created_at)
        val showUpdatedAt = findViewById<TextView>(R.id.item_updated_at)

        usersApi.getMyInfo(token).subscribeOn(Schedulers.io()).subscribe{
            val nonUidUser = it
            showName.text = nonUidUser.name
            showId.text = nonUidUser.id.toString()
            showCreatedAt.text = nonUidUser.createdAt.toString()
            showUpdatedAt.text = nonUidUser.updatedAt.toString()
        }


        val changedName = findViewById<EditText>(R.id.changed_name)
        changedName.setText("hoge?")

        val button = findViewById<Button>(R.id.apply_button)
        button.setOnClickListener{
            val inputText = changedName.text.toString()
            var isValid = regex.matches(inputText)
            if(isValid){
                usersApi.getMyInfo(token).subscribeOn(Schedulers.io()).subscribe{
                    //showName.text = it.name
                    //showUpdatedAt.text = it.updatedAt.toString()
                    Toast.makeText(this, inputText, Toast.LENGTH_SHORT).show()
                }
            }
            else{
                changedName.error = "不正な文字が使われています"
            }
        }
    }
}
