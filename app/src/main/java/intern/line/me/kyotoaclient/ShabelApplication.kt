package intern.line.me.kyotoaclient

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

class ShabelApplication:Application(){

	override fun onCreate() {
		super.onCreate()
		Realm.init(applicationContext)

	}
}