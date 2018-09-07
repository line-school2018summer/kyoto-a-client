package intern.line.me.kyotoaclient.presenter.user

import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import intern.line.me.kyotoaclient.model.entity.UserRealm
import intern.line.me.kyotoaclient.model.repository.UserRepository
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await

class GetUserInfo(val id: Long): API(){

	val api = retrofit.create(UserAPI::class.java)
	private val repo = UserRepository()

	private suspend fun getAsyncUserInfo(): UserRealm = withContext(CommonPool){
		api.getUserInfoById(id).await()
	}

	suspend fun getUserInfo(): UserRealm {
		try{
			val user =  getAsyncUserInfo()
			//TODO(ローカル参照する前にRESTを叩かないと行けない糞仕様)
			repo.getById(user.id)
			return user
		} catch (t: HttpException){
			throw Exception("update failed.")
		}
	}
}
