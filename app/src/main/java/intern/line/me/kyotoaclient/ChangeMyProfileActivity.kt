package intern.line.me.kyotoaclient

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import intern.line.me.kyotoaclient.lib.model.User
import intern.line.me.kyotoaclient.lib.api.GetMyInfo
import intern.line.me.kyotoaclient.lib.api.PutMyInfo
import kotlinx.android.synthetic.main.activity_change_my_profile.*

class ChangeMyProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_my_profile)


        val regex = Regex("[[ぁ-んァ-ヶ亜-熙] \\w ー 。 、]+")

        val sign_in_user = FirebaseAuth.getInstance().currentUser

        //非同期でユーザー情報を取ってくる
        GetMyInfo{
            setUserInfo(it)
        }.start()




        //ボタンを押したときの処理
        apply_button.setOnClickListener{

            val inputText = changed_name.text.toString()
            var isValid = regex.matches(inputText)

            if(isValid){
                PutMyInfo(inputText) {
                }.start()
                }
            else{
                changed_name.error = "不正な文字が使われています"
            }
        }
    }


    //ユーザー情報をセットする
    fun setUserInfo(user: User){
        show_name.text = user.name
        show_id.text = user.id.toString()
    }
}
