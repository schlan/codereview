package at.droelf.codereview.network

import at.droelf.codereview.model.GithubModel
import retrofit2.Response
import retrofit2.http.*
import rx.Observable


interface GithubAuthApi {

    @PUT("/authorizations/clients/{clientId}/{fingerPrint}")
    fun getOrCreateAuthToken(
            @Header("Authorization") basicAuth: String,
            @Path("clientId") clientId: String,
            @Body authRequest: GithubModel.AuthRequest,
            @Path("fingerPrint") fingerPrint: String,
            @Header("X-GitHub-OTP") otpToken: String? = null
    ): Observable<Response<GithubModel.AuthResponse>>


    @GET("/user")
    fun getUser(
            @Header("Authorization") token: String
    ): Observable<Response<GithubModel.User>>


    @GET("/user/emails")
    fun getEmails(
            @Header("Authorization") token: String
    ): Observable<Response<MutableList<GithubModel.UserEmail>>>

}