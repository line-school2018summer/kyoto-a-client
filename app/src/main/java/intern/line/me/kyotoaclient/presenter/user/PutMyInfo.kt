package intern.line.me.kyotoaclient.presenter.user

import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import intern.line.me.kyotoaclient.lib.firebase.FirebaseUtil
import intern.line.me.kyotoaclient.model.entity.User
import intern.line.me.kyotoaclient.model.repository.UserRepository
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await

class PutMyInfo(private  val name: String): API(){

	val api = retrofit.create(UserAPI::class.java)
	private val repo = UserRepository()

	private suspend fun putAsyncMyInfo(token:String): User = withContext(CommonPool){
		api.changeUserInfo(token, name).await()
	}

	suspend fun putMyInfo(): User {
		val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

		try {
			val user =  putAsyncMyInfo(token)

			repo.update(user)

			return user

		} catch (t: HttpException) {
			throw Exception("Update failed.")
		}
	}
}