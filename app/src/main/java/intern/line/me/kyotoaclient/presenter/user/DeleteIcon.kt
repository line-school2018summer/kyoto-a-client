package intern.line.me.kyotoaclient.presenter.user

import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await

class DeleteIcon(): API() {
    private val api = retrofit.create(UserAPI::class.java)

    private suspend fun deleteAsyncIcon(token: String): Boolean = withContext(CommonPool){
        api.deleteIcon(token).await()
    }

    suspend fun deleteIcon(){
        val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")
        try{
            deleteAsyncIcon(token)
        } catch (t: HttpException){
            throw Exception("DELETE ICON FAILED!")
        }
    }
}