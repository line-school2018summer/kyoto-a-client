package intern.line.me.kyotoaclient

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import intern.line.me.kyotoaclient.lib.model.User
import intern.line.me.kyotoaclient.lib.api.GetUserInfo
import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import kotlinx.android.synthetic.main.activity_get_user_profile.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GetUserProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_user_profile)

        val selectedId = intent.getLongExtra("longTapUserId", 1)
        //非同期でユーザー情報を取ってくる
        GetUserInfo(this,selectedId).start()

    }


    fun setUserInfo(set_user : User){

        show_name.text = set_user.name
        show_id.text = set_user.id.toString()
    }
}
