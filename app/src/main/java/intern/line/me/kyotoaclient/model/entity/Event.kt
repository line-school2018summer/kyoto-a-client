package intern.line.me.kyotoaclient.model.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Event(
		@PrimaryKey open var id: Long = 0,
		open var event_type: Int = 0,
		open var room_id: Long?  = null,
		open var user_id: Long? = null,
		open var message_id: Long? = null,
		open var isCompleted: Boolean = false
): RealmObject()