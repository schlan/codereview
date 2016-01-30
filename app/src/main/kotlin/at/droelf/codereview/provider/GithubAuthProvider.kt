package at.droelf.codereview.provider

import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.network.GithubAuthService
import rx.Observable
import java.util.*


class GithubAuthProvider(val githubAuthService: GithubAuthService) {

    fun authToken(username: String, password: String, totp: String?)
            : Observable<Pair<GithubModel.AuthReturnType, GithubModel.AuthResponse?>> {
        val uuid = UUID.randomUUID()
        return githubAuthService.authToken(username, password, uuid, totp)
    }
}