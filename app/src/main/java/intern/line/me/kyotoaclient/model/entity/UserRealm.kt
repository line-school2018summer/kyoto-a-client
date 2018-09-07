package intern.line.me.kyotoaclient.model.entity

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.sql.Timestamp
import java.util.Date

open class UserRealm(
		@PrimaryKey open var id: Long = 0,
		@Required open var name: String = "",
		open var created_at: Date = Date(Timestamp(2018,1,1,0,0,0,0).time),
		open var updated_at: Date = Date(Timestamp(2018,1,1,0,0,0,0).time)
) : RealmObject()
