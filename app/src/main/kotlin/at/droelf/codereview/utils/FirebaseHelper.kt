package at.droelf.codereview.utils

import com.firebase.client.AuthData
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import rx.Observable
import rx.Subscriber


interface FirebaseHelper {

    fun Firebase.authWithOAuthToken(provider: String, token: String, success: (authData: AuthData) -> Unit, error: (error: FirebaseError) -> Unit) {
        authWithOAuthToken(provider, token, object : Firebase.AuthResultHandler {
            override fun onAuthenticated(p0: AuthData) {
                success(p0)
            }

            override fun onAuthenticationError(p0: FirebaseError) {
                error(p0)
            }
        })
    }

    fun Firebase.authWithOAuthToken(provider: String, token: String): Observable<AuthData> {
        return Observable.create({ s ->
            authWithOAuthToken(provider, token, { authData ->
                s.onNext(authData)
                s.onCompleted()
            }, { error ->
                s.onError(error.toException())
            })
        })
    }

}