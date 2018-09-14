package intern.line.me.kyotoaclient.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import intern.line.me.kyotoaclient.R
import intern.line.me.kyotoaclient.model.entity.User
import intern.line.me.kyotoaclient.presenter.user.*
import kotlinx.android.synthetic.main.activity_change_my_profile.*
import kotlinx.android.synthetic.main.activity_get_user_profile.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

class ChangeMyProfileActivity : AppCompatActivity() {

    private val CHOSE_FILE_CODE: Int = 777

    private val job = Job()
    private var myId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_my_profile)

        my_profile_progress_bar.visibility = View.VISIBLE

        //非同期でユーザー情報を取ってくる
        launch(job + UI) {
            GetMyInfo().getMyInfo().let {
                setUserInfo(it)
                setImg(it.id)
                myId = it.id
            }
        }

        //ボタンを押したときの処理
        apply_button.setOnClickListener {

            val inputText = changed_name.text.toString()

            launch(job + UI) {
                PutMyInfo(inputText).putMyInfo().let{ setUserInfo(it) }
            }
        }

        //画像変更ボタン
        image_change.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            //intent.setType("file/*")
            startActivityForResult(intent, CHOSE_FILE_CODE)
        }

        //画像削除ボタン
        image_delete.setOnClickListener{
            launch(job + UI){
                DeleteIcon().deleteIcon()
                setImgByC(myId)
            }
        }
    }

    //Activityを閉じたときに非同期処理をキャンセルさせる
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    //主に画像選択後の処理
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val context = this

        try{
            if(requestCode == CHOSE_FILE_CODE && resultCode == RESULT_OK && data!=null){
                var filePath = data.getDataString()
                filePath=filePath.substring(filePath.indexOf("storage"))
                val decodedPath = URLDecoder.decode(filePath, "utf-8")
                Toast.makeText(this, decodedPath, Toast.LENGTH_LONG).show()
/*
                //TODO(file選択方法)
                val file =  File(decodedPath)
                launch(job + UI){
                    PostIcon(file).postIcon()
                    Toast.makeText(context, "updated!", Toast.LENGTH_LONG).show()
                }*/
            }
        } catch(t: UnsupportedEncodingException) {
            Toast.makeText(this, "not supported", Toast.LENGTH_SHORT).show()
        }
    }


    //ユーザー情報をセットする
    fun setUserInfo(user: User){
        my_profile_progress_bar.visibility = View.INVISIBLE
        my_name.text = user.name
    }

    //画像をセットする
    fun setImg(id: Long){
        val imageView = findViewById<ImageView>(R.id.icon)
        Glide.with(this)
                .load("https://kyoto-a-api.pinfort.me/download/icon/${id}")
                .into(imageView)
    }

    //同上
    fun setImgByC(id: Long){
        val imageView = findViewById<ImageView>(R.id.icon)
        launch(job + UI) {
            GetIcon(id).getIcon().let {
                val image = BitmapFactory.decodeStream(it)
                imageView.setImageBitmap(image)
            }
        }
    }
}
