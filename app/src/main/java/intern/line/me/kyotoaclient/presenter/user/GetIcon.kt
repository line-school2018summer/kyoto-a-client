package intern.line.me.kyotoaclient.presenter.user

import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await
import java.io.InputStream

class GetIcon(val id: Long): API() {
    private val api = retrofit.create(UserAPI::class.java)

    private suspend fun getAsyncIcon(): ResponseBody = withContext(CommonPool){
        api.getIcon(id).await()
    }

    suspend fun getIcon(): InputStream {
        try{
            val file =  getAsyncIcon()
            return file.byteStream()
        } catch (t: HttpException){
            throw Exception("update failed.")
        }
    }


}