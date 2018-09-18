package intern.line.me.kyotoaclient.presenter.event

import intern.line.me.kyotoaclient.model.entity.Event
import intern.line.me.kyotoaclient.model.entity.EventTypes
import intern.line.me.kyotoaclient.model.repository.EventRespository
import intern.line.me.kyotoaclient.model.repository.MessageRepository
import intern.line.me.kyotoaclient.presenter.message.GetMessage
import intern.line.me.kyotoaclient.presenter.room.GetMessages

class UpdateModel {

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

			}

			EventTypes.ROOM_UPDATED.ordinal ->{

			}

			EventTypes.ROOM_MEMBER_JOINED.ordinal ->{

			}

			EventTypes.ROOM_MEMBER_LEAVED.ordinal ->{

			}

			EventTypes.ROOM_MEMBER_DELETED.ordinal ->{

			}

			EventTypes.PROFILE_UPDATED.ordinal ->{

			}


		}
	}

	suspend fun updateAllModel(events: List<Event>) {
		for (event in events) {
			updateModel(event)
		}
	}
}