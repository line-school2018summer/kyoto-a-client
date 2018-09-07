package intern.line.me.kyotoaclient.model.repository

import intern.line.me.kyotoaclient.model.entity.RoomRealm
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.Sort

class RoomRepository {

	val realmConfig = RealmConfiguration.Builder()
			.deleteRealmIfMigrationNeeded()
			.build()
	val mRealm = Realm.getInstance(realmConfig)

	fun getById(id: Long): RoomRealm? {
		val r_room = mRealm.where(RoomRealm::class.java).equalTo("id", id).findFirst()
		return r_room
	}

	fun getAll(): List<RoomRealm> {
		val rooms: MutableList<RoomRealm> = mutableListOf<RoomRealm>()
		val db_rooms = mRealm.where(RoomRealm::class.java).findAll().sort("created_at", Sort.ASCENDING)

		db_rooms.forEach {
			rooms.add(it)!!
		}
		return rooms
	}


	fun create(room: RoomRealm) {
		mRealm.executeTransaction {
			var create_room = mRealm.copyToRealmOrUpdate(room)

			create_room.name = room.name
			create_room.created_at = room.created_at
			create_room.updated_at = room.updated_at

		}
	}

	fun update(room: RoomRealm) {
		mRealm.executeTransaction {
			var update_room = mRealm.copyToRealmOrUpdate(room)

			update_room.name = room.name
			update_room.created_at = room.created_at
			update_room.updated_at = room.updated_at

		}
	}

	fun updateAll(rooms: List<RoomRealm>) {
		for (room in rooms) {
			update(room)
		}
	}

	fun delete(room: RoomRealm) {
		mRealm.executeTransaction {
			var delete_room = mRealm.where(RoomRealm::class.java).equalTo("id", room.id).findAll()
			delete_room.deleteFromRealm(0)
		}
	}
}

