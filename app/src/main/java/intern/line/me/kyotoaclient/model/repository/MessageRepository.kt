package intern.line.me.kyotoaclient.model.repository

import intern.line.me.kyotoaclient.model.entity.Message
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.Sort

class MessageRepository {

	val realmConfig = RealmConfiguration.Builder()
			.deleteRealmIfMigrationNeeded()
			.build()
	val mRealm = Realm.getInstance(realmConfig)

	fun getById(id: Long): Message? {
		val r_message = mRealm.where(Message::class.java).equalTo("id", id).findFirst()
		return r_message
	}

	fun getAll(room_id : Long) : List<Message> {
		val messages: MutableList<Message> = mutableListOf<Message>()
		val db_messages = mRealm.where(Message::class.java).equalTo("room_id",room_id).findAll().sort("created_at", Sort.ASCENDING)

		db_messages.forEach{
			messages.add(it!!)
		}
		return messages
	}


	fun create(message: Message) {
		mRealm.executeTransaction {
			var create_message = mRealm.copyToRealmOrUpdate(message)!!

			val repo = UserRepository()
			val user = repo.getUserRealmById(message.user_id) ?: mRealm.copyToRealm(message.user)
			create_message.user = user
		}
	}

	fun update(message: Message) {
		mRealm.executeTransaction {
			var update_message = mRealm.copyToRealmOrUpdate(message)!!

			val repo = UserRepository()
			val user = repo.getUserRealmById(message.user_id) ?: mRealm.copyToRealm(message.user)
			update_message.user = user
		}
	}

	fun updateAll(messages: List<Message>) {
		for (message in messages) {
			update(message)
		}
	}

	fun delete(message: Message) {
		mRealm.executeTransaction {
			var delete_message = mRealm.where(Message::class.java).equalTo("id", message.id).findAll()
			delete_message.deleteFromRealm(0)
		}
	}
}