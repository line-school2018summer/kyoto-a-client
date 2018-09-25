package intern.line.me.kyotoaclient.presenter.user

import intern.line.me.kyotoaclient.lib.api.interfaces.UserAPI
import intern.line.me.kyotoaclient.model.entity.User
import intern.line.me.kyotoaclient.model.repository.UserRepository
import intern.line.me.kyotoaclient.presenter.API
import io.realm.RealmResults
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await

class SearchUsers: API() {

	private val api = retrofit.create(UserAPI::class.java)
	val repo = UserRepository()

	private suspend fun getAsyncUsersList(name: String): List<User> = withContext(CommonPool) {
		api.searchUsers(name).await()
	}

	suspend fun getUsersList(name : String): List<User> {
		try {
			return getAsyncUsersList(name)

		} catch (t: HttpException) {
			throw Exception("update failed.")
		}
	}

	fun getUsersListFromDB(name : String)  : RealmResults<User>{
		return repo.getUsersFromName(name)
	}
}