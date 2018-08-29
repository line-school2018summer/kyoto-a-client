package intern.line.me.kyotoaclient.lib.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.coroutines.experimental.suspendCoroutine

class FirebaseUtil{
    suspend fun getToken(user: FirebaseUser) : String? = suspendCoroutine{ cont ->

        user.getIdToken(true)
                .addOnCompleteListener {
                    Log.d("Token", "complete getIdToken")

                    if (it.isSuccessful) {
                        cont.resume( it.result.token)
                    } else {
                        throw Exception("can't get token.")
                    }
                }
    }

}