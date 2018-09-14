package intern.line.me.kyotoaclient.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.model.entity.User
import intern.line.me.kyotoaclient.presenter.user.GetMyInfo
import intern.line.me.kyotoaclient.presenter.user.PutMyInfo
import kotlinx.android.synthetic.main.activity_change_my_profile.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class ChangeMyProfileActivity : AppCompatActivity() {

    private val job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_my_profile)

        my_profile_progress_bar.visibility = View.VISIBLE

        //非同期でユーザー情報を取ってくる
        launch(job + UI) {
            GetMyInfo().getMyInfo().let {
                setUserInfo(it)
                setImg(it.id)
            }
        }

        //ボタンを押したときの処理
        apply_button.setOnClickListener {

            val inputText = changed_name.text.toString()

            launch(job + UI) {
                PutMyInfo(inputText).putMyInfo().let{ setUserInfo(it) }
            }
        }
    }



    //Activityを閉じたときに非同期処理をキャンセルさせる
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    //ユーザー情報をセットする
    fun setUserInfo(user: User){
        my_profile_progress_bar.visibility = View.INVISIBLE
        my_name.text = user.name
    }

    fun setImg(id: Long){
        val imageView = findViewById<ImageView>(R.id.icon)
        Glide.with(this)
                .load("https://kyoto-a-api.pinfort.me/download/icon/${id}")
                .into(imageView)
    }
}
