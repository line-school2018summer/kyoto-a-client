package intern.line.me.kyotoaclient.model.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Message(
		@PrimaryKey open var id: Long = 0,
		open var room_id: Long = 0,
		open var user_id: Long = 0,
		open var text: String = "",
		open var user_name: String = "",
		// 61475587200000は 2018/1/1 00:00のtimestamp
		open var created_at: Date = Date(61475587200000),
		open var updated_at: Date = Date(61475587200000)
):RealmObject()