package at.droelf.codereview.provider

import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.network.GithubAuthService
import rx.Observable
import java.util.*


class GithubAuthProvider(val githubAuthService: GithubAuthService) {

    fun authToken(username: String, password: String, totp: String?)
            : Observable<Triple<GithubModel.AuthReturnType, GithubModel.AuthResponse?, UUID>> {
        val uuid = UUID.randomUUID()
        return githubAuthService.authToken(username, password, uuid, totp).map{ Triple(it.first, it.second, uuid) }
    }

    fun user(token: String): Observable<GithubModel.User> {
        return githubAuthService.user(token)
    }

    fun emails(token: String): Observable<String> {
        return githubAuthService.email(token)
                    .map { email -> email.filter { it.primary }.firstOrNull()?.email ?: "<no email>" }
    }

}