package intern.line.me.kyotoaclient.model.repository

import android.util.Log
import intern.line.me.kyotoaclient.model.entity.Event
import intern.line.me.kyotoaclient.model.entity.EventTypes
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.Sort

class EventRespository {

	private val realmConfig = RealmConfiguration.Builder()
			.deleteRealmIfMigrationNeeded()
			.build()
	private val mRealm = Realm.getInstance(realmConfig)

	fun getById(id: Long): Event? {
		return mRealm.where(Event::class.java).equalTo("id", id).findFirstAsync()
	}

	fun getAll() : RealmResults<Event> {
		return  mRealm.where(Event::class.java).sort("id", Sort.ASCENDING).findAllAsync()
	}

	//完了していないイベントの中で最小のidを返す
	fun getLatestMessageEvent(room_id: Long) : Event?{
		val eTypes: Array<out Int> = arrayOf(
				EventTypes.MESSAGE_SENT.ordinal,
				EventTypes.MESSAGE_UPDATED.ordinal,
				EventTypes.MESSAGE_DELETED.ordinal
		)
		val retVal: Event?
		retVal = try {
			mRealm.where(Event::class.java).equalTo("isCompleted",false).`in`("event_type", eTypes).equalTo("room_id", room_id).findAll().sort("id", Sort.ASCENDING).last()
		}catch(e : Throwable){
			Log.e("getLatest","can't get latest event",e)
			null
		}
		return retVal
	}

	//完了していないイベントの中で最小のidを返す
	fun getLatestForRooms(): Event? {
		val retVal: Event?
		retVal = try {
			val eTypes: Array<out Int> = arrayOf(
					EventTypes.ROOM_CREATED.ordinal,
					EventTypes.ROOM_UPDATED.ordinal,
					EventTypes.ROOM_MEMBER_JOINED.ordinal,
					EventTypes.ROOM_MEMBER_LEAVED.ordinal,
					EventTypes.ROOM_MEMBER_DELETED.ordinal,
					EventTypes.ROOM_ICON_UPDATED.ordinal
			)

			mRealm.where(Event::class.java).equalTo("isCompleted",false).`in`("event_type", eTypes).findAll().sort("id", Sort.ASCENDING).last()
		}catch(e : Throwable){
			Log.e("getLatest","can't get latest event",e)
			null
		}
		return retVal
	}


	fun update(event: Event) {
		mRealm.executeTransaction {
			mRealm.copyToRealmOrUpdate(event)
		}
	}

	fun complete(event: Event) {
		mRealm.executeTransaction {
			mRealm.copyToRealmOrUpdate(event)
			event.isCompleted = true
		}
	}

	fun updateAll(events: List<Event>) {
		for (event in events) {
			update(event)
		}
	}

	fun delete(event: Event) {
		mRealm.executeTransaction {
			mRealm.where(Event::class.java).equalTo("id", event.id).findAllAsync().apply{
				this.deleteFromRealm(0)
			}
		}
	}
}