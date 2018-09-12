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

class GetUserList: API() {
	private val api = retrofit.create(UserAPI::class.java)
	private val  repo = UserRepository()

	private suspend fun getAsyncUsersList(): List<User> = withContext(CommonPool) {
		api.getUsers().await()
	}

	suspend fun getUsersList(): List<User> {
		try {
			val users =  getAsyncUsersList()

			repo.updateAll(users)
			return users

		} catch (t: HttpException) {
			throw Exception("update failed.")
		}
	}

	fun getUsersListFromDb(): RealmResults<User> {
			return repo.getAll()
	}
}
