package intern.line.me.kyotoaclient.model.repository

import intern.line.me.kyotoaclient.model.entity.Room
import io.realm.Realm
import io.realm.RealmConfiguration
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

	fun getAll(): List<Room> {
		val rooms: MutableList<Room> = mutableListOf<Room>()
		val db_rooms = mRealm.where(Room::class.java).findAll().sort("created_at", Sort.ASCENDING)

		db_rooms.forEach {
			rooms.add(it)!!
		}
		return rooms
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

		}
	}

	fun updateAll(rooms: List<Room>) {
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

