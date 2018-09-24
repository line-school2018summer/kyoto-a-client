package intern.line.me.kyotoaclient.lib.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.coroutines.experimental.suspendCoroutine

class FirebaseUtil{
    suspend fun getToken() : String? = suspendCoroutine{ cont ->
        val auth = FirebaseAuth.getInstance()!!
        val user = auth.currentUser ?: throw Exception("Can't get current user.")

        user.getIdToken(true)
                .addOnCompleteListener {
                    Log.d("Token", "complete getIdToken")

                    if (it.isSuccessful) {
                        cont.resume( it.result.token)
                    } else {
                        Log.d("FirebaseUtil","can't get token.")
                    }
                }
    }

}