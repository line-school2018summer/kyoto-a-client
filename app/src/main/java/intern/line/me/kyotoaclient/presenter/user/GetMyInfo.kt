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

class GetMyInfo: API(){

	private val api = retrofit.create(UserAPI::class.java)
	private val repo = UserRepository()

	private suspend fun getAsyncMyInfo(token : String): User = withContext(CommonPool){
		api.getMyInfo(token).await()
	}

	suspend fun getMyInfo(): User {
		val token = FirebaseUtil().getToken() ?: throw Exception("can't get token.")

		try {

			//TODO(ローカル参照する前にRESTを叩かないと行けない糞仕様)
			val user =  getAsyncMyInfo(token)

			val realm_user = repo.getById(user.id)

			if (realm_user != null){
				return realm_user
			}else{
				return user
			}

		} catch (t: HttpException) {
			throw Exception("Update failed.")
		}
	}
}
