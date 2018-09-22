package intern.line.me.kyotoaclient.lib.api.interfaces

import intern.line.me.kyotoaclient.model.entity.Event
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface EventAPI{

	@GET("/events/{id}")
	fun getEventById(
			@Path("id")id: Long,
			@Header("Token") Token: String
	): Call<Event>

	@GET("events/rooms")
	fun getRoomEvents(
			@Header("Token") Token: String,
			@Query("since_id") since_id: String
	): Call<List<Event>>

	@GET("/events/rooms/{id}/messages")
	fun getMessageEvent(
			@Path("id") id: Long,
			@Header("Token") Token: String,
			@Query("since_id") since_id: String
	): Call<List<Event>>
}