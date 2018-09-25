package intern.line.me.kyotoaclient.model.repository

import intern.line.me.kyotoaclient.model.entity.Message
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.Sort

class MessageRepository {

	private val realmConfig = RealmConfiguration.Builder()
			.deleteRealmIfMigrationNeeded()
			.build()


	fun getById(id: Long): Message? {
		val mRealm = Realm.getInstance(realmConfig)
		return  mRealm.where(Message::class.java).equalTo("id", id).findFirst().apply {
			mRealm.close()
		}
	}

	fun getAll(room_id : Long) : RealmResults<Message> {
		val mRealm = Realm.getInstance(realmConfig)
		mRealm.close()

		return mRealm.where(Message::class.java).equalTo("room_id",room_id).findAllAsync().sort("created_at", Sort.ASCENDING)

	}


	fun create(message: Message) {
		val mRealm = Realm.getInstance(realmConfig)

		mRealm.executeTransaction {
			mRealm.copyToRealmOrUpdate(message)!!
		}
		mRealm.close()

	}

	fun update(message: Message) {
		val mRealm = Realm.getInstance(realmConfig)

		mRealm.executeTransaction {
			mRealm.copyToRealmOrUpdate(message)!!
		}
		mRealm.close()

	}

	fun updateAll(messages: List<Message>) {
		for (message in messages) {
			update(message)
		}
	}

	fun delete(message_id: Long) {
		val mRealm = Realm.getInstance(realmConfig)
		mRealm.executeTransaction {
			mRealm.where(Message::class.java).equalTo("id", message_id).findFirst()?.deleteFromRealm()
		}
		mRealm.close()
	}
}