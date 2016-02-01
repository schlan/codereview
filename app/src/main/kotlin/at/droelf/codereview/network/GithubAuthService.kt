package at.droelf.codereview.network

import android.util.Base64
import at.droelf.codereview.model.GithubModel
import rx.Observable
import java.util.*


class GithubAuthService(
        private val githubAuthApi: GithubAuthApi,
        private val clientId: String,
        private val authRequest: GithubModel.AuthRequest) {


    fun authToken(username: String, password: String, fingerPrint: UUID, totp: String? = null): Observable<Pair<GithubModel.AuthReturnType, GithubModel.AuthResponse?>> {
        return githubAuthApi.getOrCreateAuthToken(basicAuth(username, password), clientId, authRequest, fingerPrint.toString(), totp).map { response ->
            when (response.code()) {
                401 -> {
                    val header = response.headers().get("X-GitHub-OTP") ?: ""
                    if (header.length > 0) {
                        val twoFactorType = header.split(";").last().trim()

                        val type = when (twoFactorType) {
                            "sms" -> GithubModel.AuthReturnType.TwoFactorSms
                            "app" -> GithubModel.AuthReturnType.TwoFactorApp
                            else -> GithubModel.AuthReturnType.TwoFactorUnknown
                        }
                        Pair(type, null)

                    } else {
                        Pair(GithubModel.AuthReturnType.Error, null)
                    }
                }
                200, 201 -> {
                    Pair(GithubModel.AuthReturnType.Success, response.body())
                }
                else -> {
                    Pair(GithubModel.AuthReturnType.Error, null)
                }
            }
        }
    }

    fun user(token: String): Observable<GithubModel.User> {
        return githubAuthApi.getUser(token(token)).map { it.body() }
    }

    fun token(token: String): String {
        return "token $token"
    }

    private fun basicAuth(username: String, password: String): String {
        val creds = "$username:$password"
        return "Basic ${Base64.encodeToString(creds.toByteArray(), Base64.NO_WRAP)}"
    }
}