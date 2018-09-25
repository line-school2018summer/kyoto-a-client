package intern.line.me.kyotoaclient.model.repository

import intern.line.me.kyotoaclient.model.entity.Room
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.Sort

class RoomRepository {

	private val realmConfig = RealmConfiguration.Builder()
			.deleteRealmIfMigrationNeeded()
			.build()
	private val mRealm = Realm.getInstance(realmConfig)

	fun getById(id: Long): Room? {
		return mRealm.where(Room::class.java).equalTo("id", id).findFirst()
	}

	fun getAll(): RealmResults<Room> {
		return mRealm.where(Room::class.java).findAll().sort("last_message_created_at", Sort.DESCENDING)
	}


	fun create(room: Room) {
		mRealm.executeTransaction {
			val createRoom = mRealm.copyToRealmOrUpdate(room)

			createRoom.name = room.name
			createRoom.created_at = room.created_at
			createRoom.updated_at = room.updated_at

		}
	}

	fun update(room: Room) {
		mRealm.executeTransaction {
			val updateRoom = mRealm.copyToRealmOrUpdate(room)

			updateRoom.name = room.name
			updateRoom.created_at = room.created_at
			updateRoom.updated_at = room.updated_at
			updateRoom.last_message_text = room.last_message_text
			updateRoom.last_message_created_at = room.last_message_created_at
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
			val deleteRoom = mRealm.where(Room::class.java).equalTo("id", room.id).findAll()
			deleteRoom.deleteFromRealm(0)
		}
	}
}

