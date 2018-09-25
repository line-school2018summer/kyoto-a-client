package intern.line.me.kyotoaclient.model.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.sql.Timestamp
import java.util.*

open class Room(
		@PrimaryKey open  var id: Long =0,
		open var name: String = "",
		// 61475587200000は 2018/1/1 00:00のtimestamp
		open var created_at: Date = Date(61475587200000),
		open var updated_at: Date = Date(61475587200000),
		open var last_message_text: String? = null,
		open var last_message_created_at : Date? = Date(61475587200000)
):RealmObject()