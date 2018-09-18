package intern.line.me.kyotoaclient.model.repository

import intern.line.me.kyotoaclient.model.entity.Event
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.Sort

class EventRespository {

	val realmConfig = RealmConfiguration.Builder()
			.deleteRealmIfMigrationNeeded()
			.build()
	val mRealm = Realm.getInstance(realmConfig)

	fun getById(id: Long): Event? {
		return mRealm.where(Event::class.java).equalTo("id", id).findFirst()
	}

	fun getAll() : RealmResults<Event> {
		return  mRealm.where(Event::class.java).sort("id", Sort.ASCENDING).findAll()
	}

	fun getLatest() : Event?{
		return mRealm.where(Event::class.java).sort("id", Sort.DESCENDING).findFirst()
	}


	fun update(event: Event) {
		mRealm.executeTransaction {
			mRealm.copyToRealmOrUpdate(event)
		}
	}

	fun updateAll(events: List<Event>) {
		for (event in events) {
			update(event)
		}
	}

	fun delete(event: Event) {
		mRealm.executeTransaction {
			var delete_event = mRealm.where(Event::class.java).equalTo("id", event.id).findAll()
			delete_event.deleteFromRealm(0)
		}
	}
}