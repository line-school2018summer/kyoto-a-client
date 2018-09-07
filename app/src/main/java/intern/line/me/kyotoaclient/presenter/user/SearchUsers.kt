package intern.line.me.kyotoaclient.presenter.user

import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import intern.line.me.kyotoaclient.model.entity.User
import intern.line.me.kyotoaclient.presenter.API
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await

class SearchUsers(val name: String): API() {

	val api = retrofit.create(UserAPI::class.java)

	private suspend fun getAsyncUsersList(): List<User> = withContext(CommonPool) {
		api.searchUsers(name).await()
	}

	suspend fun getUsersList(): List<User> {
		try {
			return getAsyncUsersList()

		} catch (t: HttpException) {
			throw Exception("update failed.")
		}
	}
}