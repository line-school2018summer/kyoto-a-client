package intern.line.me.kyotoaclient.presenter.event

import android.content.Context
import android.util.Log
import intern.line.me.kyotoaclient.lib.util.IconFiles
import intern.line.me.kyotoaclient.model.entity.Event
import intern.line.me.kyotoaclient.model.entity.EventTypes
import intern.line.me.kyotoaclient.model.repository.EventRespository
import intern.line.me.kyotoaclient.model.repository.MessageRepository
import intern.line.me.kyotoaclient.presenter.message.GetMessage
import intern.line.me.kyotoaclient.presenter.room.GetRooms

class UpdateModel(private val context: Context) {

	private val eventRepo = EventRespository()
	private val messageRepo = MessageRepository()

	suspend fun updateModel(event: Event) {
		eventRepo.update(event)

		when(event.event_type){
			EventTypes.MESSAGE_SENT.ordinal ->{
				Log.d("MESSAGE_SENT","MESSAGE_SENT")
				if(event.room_id != null) {
					GetMessage().getMessage(event.message_id!!)
					eventRepo.complete(event)
				}
			}

			EventTypes.MESSAGE_UPDATED.ordinal ->{
				Log.d("MESSAGE_UPDATED","MESSAGE_UPDATED")

				if(event.message_id != null) {
					GetMessage().getMessage(event.message_id!!)
					eventRepo.complete(event)
				}
			}

			EventTypes.MESSAGE_DELETED.ordinal ->{
				Log.d("MESSAGE_DELETED","MESSAGE_DELETED")
				if(event.message_id != null){
					messageRepo.delete(event.message_id!!)
					eventRepo.complete(event)
				}
			}

			EventTypes.ROOM_CREATED.ordinal ->{
				Log.d("ROOM_CREATED","ROOM_CREATED")
				GetRooms().getRooms()
				eventRepo.complete(event)
			}

			EventTypes.ROOM_UPDATED.ordinal ->{
				Log.d("ROOM_UPDATED","ROOM_UPDATED")
				GetRooms().getRooms()
				eventRepo.complete(event)
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
				Log.d("ROOM_ICON_UPDATED","ROOM_ICON_UPDATED")
				val roomId = event.room_id
				if (roomId != null) {
					IconFiles().updateRoomIcon(context, roomId)
					eventRepo.complete(event)
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