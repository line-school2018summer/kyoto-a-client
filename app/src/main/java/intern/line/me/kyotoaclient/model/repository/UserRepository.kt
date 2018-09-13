package intern.line.me.kyotoaclient.model.repository

import intern.line.me.kyotoaclient.model.entity.User
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.Sort

class UserRepository {

	val realmConfig = RealmConfiguration.Builder()
			.deleteRealmIfMigrationNeeded()
			.build()
	val mRealm = Realm.getInstance(realmConfig)

	fun getById(id: Long): User? {
		val r_user = mRealm.where(User::class.java).equalTo("id", id).findFirst()
		return r_user
	}

	fun getUserRealmById(id: Long): User? {
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
			var create_user = mRealm.copyToRealmOrUpdate(user)
			create_user.name = user.name
			create_user.created_at = user.created_at
			create_user.updated_at = user.updated_at

		}
	}

	fun update(user: User) {
		mRealm.executeTransaction {
			var update_user = mRealm.copyToRealmOrUpdate(user)

			update_user.name = user.name
			update_user.created_at = user.created_at
			update_user.updated_at = user.updated_at

		}
	}

	fun updateAll(users: List<User>) {
		for (user in users) {
			update(user)
		}
	}

	fun delete(user: User) {
		mRealm.executeTransaction {
			var delete_user = mRealm.where(User::class.java).equalTo("id", user.id).findAll()
			delete_user.deleteFromRealm(0)
		}
	}
}