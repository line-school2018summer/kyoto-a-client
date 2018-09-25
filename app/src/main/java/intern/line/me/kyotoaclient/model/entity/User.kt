package intern.line.me.kyotoaclient.model.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.Date

open class User(
		@PrimaryKey open var id: Long = 0,
		@Required open var name: String = "",
		// 61475587200000は 2018/1/1 00:00のtimestamp
		open var created_at: Date = Date(61475587200000),
		open var updated_at: Date = Date(61475587200000)
) : RealmObject()
