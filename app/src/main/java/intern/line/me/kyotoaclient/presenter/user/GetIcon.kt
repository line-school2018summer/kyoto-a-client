package intern.line.me.kyotoaclient.presenter.user

import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.model.entity.User
import intern.line.me.kyotoaclient.model.repository.UserRepository
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import ru.gildor.coroutines.retrofit.await
import ru.gildor.coroutines.retrofit.awaitResponse
import java.io.InputStream

class GetIcon(val id: Long): API() {
    val api = retrofit.create(UserAPI::class.java)
    private val repo = UserRepository()

    private suspend fun getAsyncIcon(): ResponseBody = withContext(CommonPool){
        api.getIcon(id).await()
    }

    suspend fun getIcon(): InputStream {
        try{
            val file =  getAsyncIcon()
            val _is : InputStream = file.byteStream()
            return _is
        } catch (t: HttpException){
            throw Exception("update failed.")
        }
    }


}