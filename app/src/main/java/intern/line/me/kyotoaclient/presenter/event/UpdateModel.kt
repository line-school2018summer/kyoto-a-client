package intern.line.me.kyotoaclient.presenter.event

import android.content.Context
import intern.line.me.kyotoaclient.lib.util.IconFiles
import intern.line.me.kyotoaclient.model.entity.Event
import intern.line.me.kyotoaclient.model.entity.EventTypes
import intern.line.me.kyotoaclient.model.repository.EventRespository
import intern.line.me.kyotoaclient.model.repository.MessageRepository
import intern.line.me.kyotoaclient.presenter.message.GetMessage
import intern.line.me.kyotoaclient.presenter.room.GetRooms

class UpdateModel(private val context: Context) {

	private val event_repo = EventRespository()
	private val message_repo = MessageRepository()

	suspend fun updateModel(event: Event) {
		event_repo.update(event)

		when(event.event_type){
			EventTypes.MESSAGE_SENT.ordinal ->{
				if(event.room_id != null) {
					GetMessage().getMessage(event.message_id!!)
				}
			}

			EventTypes.MESSAGE_UPDATED.ordinal ->{
				if(event.message_id != null) {
					GetMessage().getMessage(event.message_id!!)
				}
			}

			EventTypes.MESSAGE_DELETED.ordinal ->{
				if(event.message_id != null){
					message_repo.delete(event.message_id!!)
				}
			}

			/*
				ここより下は今はいらないかなって感じ
			 */

			EventTypes.ROOM_CREATED.ordinal ->{
				GetRooms().getRooms()
			}

			EventTypes.ROOM_UPDATED.ordinal ->{
				GetRooms().getRooms()
			}

			EventTypes.ROOM_MEMBER_JOINED.ordinal ->{

			}

			EventTypes.ROOM_MEMBER_LEAVED.ordinal ->{

			}

			EventTypes.ROOM_MEMBER_DELETED.ordinal ->{

			}

			EventTypes.PROFILE_UPDATED.ordinal ->{

			}

			EventTypes.ROOM_ICON_UPDATED.ordinal -> {
				val room_id = event.room_id
				if (room_id != null) {
					IconFiles().updateRoomIcon(context, room_id)
				}
			}
		}
	}

	suspend fun updateAllModel(events: List<Event>) {
		for (event in events) {
			updateModel(event)
		}
	}
}