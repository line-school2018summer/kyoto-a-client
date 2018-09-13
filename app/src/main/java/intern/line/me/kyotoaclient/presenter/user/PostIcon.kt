package intern.line.me.kyotoaclient.presenter.user

import okhttp3.MultipartBody
import retrofit2.http.Multipart

class PostIcon {
    val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file)


}