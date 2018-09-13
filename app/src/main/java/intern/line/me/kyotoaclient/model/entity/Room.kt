package intern.line.me.kyotoaclient.model.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.sql.Timestamp
import java.util.*

open class Room(
		@PrimaryKey open  var id: Long =0,
		open var name: String = "",
		open var created_at: Date = Date(Timestamp(2018,1,1,0,0,0,0).time),
		open var updated_at: Date = Date(Timestamp(2018,1,1,0,0,0,0).time),
		open var last_message: Message? = null
):RealmObject()