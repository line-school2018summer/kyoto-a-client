package intern.line.me.kyotoaclient.model.repository

import android.util.Log
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
		return mRealm.where(Event::class.java).equalTo("id", id).findFirstAsync()
	}

	fun getAll() : RealmResults<Event> {
		return  mRealm.where(Event::class.java).sort("id", Sort.ASCENDING).findAllAsync()
	}

	fun getLatest() : Event?{
		try {
			return mRealm.where(Event::class.java).findAll().sort("id", Sort.ASCENDING).last()
		}catch(e : Throwable){
			Log.e("getLatest","can't get latest event",e)
			return null
		}
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
			var delete_event = mRealm.where(Event::class.java).equalTo("id", event.id).findAllAsync()
			delete_event.deleteFromRealm(0)
		}
	}
}