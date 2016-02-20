package at.droelf.codereview.ui.fragment

import android.support.v4.app.FragmentManager
import at.droelf.codereview.dagger.activity.MainActivityComponent
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.Model
import at.droelf.codereview.provider.GithubAuthProvider
import at.droelf.codereview.storage.GithubUserStorage
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.utils.RxHelper
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*


class LoginFragmentController(val mainActivityController: MainActivityController,
                              val githubAuthProvider: GithubAuthProvider,
                              val githubUserStorage: GithubUserStorage) : RxHelper {

    fun initLogin(mainActivityComponent: MainActivityComponent, fm: FragmentManager, auth: Model.GithubAuth) {
        mainActivityController.createUserComponent(mainActivityComponent, auth)
        mainActivityController.displayNotificationFragment(fm)
    }

    fun getToken(username: String, password: String, totpToken: String? = null)
            : Observable<Triple<GithubModel.AuthReturnType, GithubModel.AuthResponse?, UUID>> {
        return githubAuthProvider.authToken(username, password, totpToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getUserAndStoreUserData(auth: GithubModel.AuthResponse, uuid: UUID): Observable<Model.GithubAuth> {
        val data = Observable.combineLatest(
                githubAuthProvider.user(auth.token), githubAuthProvider.emails(auth.token),
                { u, e -> Pair(u, e) }
        )
        return data
                .flatMap { storeUserData(auth, it.first, it.second, uuid) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    fun storeUserData(auth: GithubModel.AuthResponse, user: GithubModel.User, email: String, uuid: UUID): Observable<Model.GithubAuth> {
        return Observable.create {
            val githubAuth = Model.GithubAuth(auth, user, email, uuid)
            githubUserStorage.storeUser(githubAuth)
            it.onNext(githubAuth)
            it.onCompleted()
        }
    }
}