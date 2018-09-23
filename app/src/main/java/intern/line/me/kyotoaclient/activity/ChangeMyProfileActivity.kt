package intern.line.me.kyotoaclient.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.provider.DocumentsContract
import android.provider.MediaStore
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
    lateinit var file: File

    private val job = Job()
    private var myId = 0L
    val regex = Regex("[[ぁ-んァ-ヶ亜-熙] \\w ー 。 、]+")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_my_profile)

        my_profile_progress_bar.visibility = View.VISIBLE

        //非同期でユーザー情報を取ってくる
        launch(job + UI) {
            GetMyInfo().getMyInfo().let {
                setUserInfo(it)
                setImgByC(it.id)
                myId = it.id
            }
        }

        if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf<String>(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }

        //ボタンを押したときの処理
        apply_button.setOnClickListener {

            val inputText = changed_name.text.toString()

            launch(job + UI) {
                if (regex.matches(inputText) ) {
                    PutMyInfo(inputText).putMyInfo().let { setUserInfo(it) }
                    changed_name.text.clear()
                } else {
                    changed_name.error = "不正な名前です"
                }
            }
        }

        //画像変更ボタン
        image_change.setOnClickListener{


            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, CHOSE_FILE_CODE)
        }

        //画像削除ボタン
        image_delete.setOnClickListener {

            launch(job + UI) {
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

        try {
            if (requestCode == CHOSE_FILE_CODE && resultCode == RESULT_OK && data != null) {
                val uri = Uri.parse(data.dataString)

                val column = arrayOf(MediaStore.Images.Media.DATA)
                val cursor: Cursor?
                val checkUri: String = uri.toString().replace("content://", "")
                if (checkUri.indexOf(':') != -1 || checkUri.indexOf("%3A") != -1) {
                    val fileId = DocumentsContract.getDocumentId(uri)
                    println(uri)
                    val id = fileId.split(":")[1]
                    val selector = MediaStore.Images.Media._ID + "=?"
                    cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, selector, arrayOf(id), null)
                } else {
                    cursor = context.contentResolver.query(uri, column, null, null, null)
                }
                var path: String? = null
                val columnIndex = cursor.getColumnIndex(column[0])
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        path = cursor.getString(columnIndex)
                    }
                    cursor.close()
                    if (path != null) {
                        file = File(path)
                    }

                    launch(job + UI) {
                        PostIcon(file).postIcon()
                        setImgByC(myId)
                    }
                } else{
                    Toast.makeText(this, "適切でないファイル形式です", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (t: UnsupportedEncodingException) {
            Toast.makeText(this, "not supported", Toast.LENGTH_SHORT).show()
        }
    }

    //ユーザー情報をセットする
    fun setUserInfo(user: User){
        my_profile_progress_bar.visibility = View.INVISIBLE
        changed_name.hint = user.name
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
