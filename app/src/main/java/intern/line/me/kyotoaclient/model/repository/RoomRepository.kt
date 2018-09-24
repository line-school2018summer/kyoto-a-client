package intern.line.me.kyotoaclient.model.repository

import intern.line.me.kyotoaclient.model.entity.Room
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.Sort

class RoomRepository {

	val realmConfig = RealmConfiguration.Builder()
			.deleteRealmIfMigrationNeeded()
			.build()
	val mRealm = Realm.getInstance(realmConfig)

	fun getById(id: Long): Room? {
		val r_room = mRealm.where(Room::class.java).equalTo("id", id).findFirst()
		return r_room
	}

	fun getAll(): RealmResults<Room> {
		return mRealm.where(Room::class.java).findAll().sort("last_message_created_at", Sort.DESCENDING)
	}


	fun create(room: Room) {
		mRealm.executeTransaction {
			var create_room = mRealm.copyToRealmOrUpdate(room)

			create_room.name = room.name
			create_room.created_at = room.created_at
			create_room.updated_at = room.updated_at

		}
	}

	fun update(room: Room) {
		mRealm.executeTransaction {
			var update_room = mRealm.copyToRealmOrUpdate(room)

			update_room.name = room.name
			update_room.created_at = room.created_at
			update_room.updated_at = room.updated_at
			update_room.last_message_text = room.last_message_text
			update_room.last_message_created_at = room.last_message_created_at
		}
	}

	fun updateAll(rooms: List<Room>) {
		mRealm.executeTransaction{
			mRealm.delete(Room::class.java)
		}
		for (room in rooms) {
			update(room)
		}
	}

	fun delete(room: Room) {
		mRealm.executeTransaction {
			var delete_room = mRealm.where(Room::class.java).equalTo("id", room.id).findAll()
			delete_room.deleteFromRealm(0)
		}
	}
}

