package intern.line.me.kyotoaclient.model.repository

import intern.line.me.kyotoaclient.model.entity.User
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.Sort

class UserRepository {

	private val realmConfig = RealmConfiguration.Builder()
			.deleteRealmIfMigrationNeeded()
			.build()
	private val mRealm = Realm.getInstance(realmConfig)

	fun getById(id: Long): User? {
		return  mRealm.where(User::class.java).equalTo("id", id).findFirst()
	}

	fun getUsersListExcludeId(id: Long): RealmResults<User>{
		return mRealm.where(User::class.java).notEqualTo("id",id).findAll()
	}

	fun getAll() : RealmResults<User> {
		return  mRealm.where(User::class.java).sort("id", Sort.ASCENDING).findAll()
	}

	fun getUsersFromName(name: String) : RealmResults<User>{
		return mRealm.where(User::class.java).contains("name",name).findAll()
	}


	fun create(user: User) {
		mRealm.executeTransaction {
			val createUser = mRealm.copyToRealmOrUpdate(user)
			createUser.name = user.name
			createUser.created_at = user.created_at
			createUser.updated_at = user.updated_at

		}
	}

	fun update(user: User) {
		mRealm.executeTransaction {
			val updateUser = mRealm.copyToRealmOrUpdate(user)

			updateUser.name = user.name
			updateUser.created_at = user.created_at
			updateUser.updated_at = user.updated_at

		}
	}

	fun updateAll(users: List<User>) {
		for (user in users) {
			update(user)
		}
	}

	fun delete(user: User) {
		mRealm.executeTransaction {
			val deleteUser = mRealm.where(User::class.java).equalTo("id", user.id).findAll()
			deleteUser.deleteFromRealm(0)
		}
	}
}