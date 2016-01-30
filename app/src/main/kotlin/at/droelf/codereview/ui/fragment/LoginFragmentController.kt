package at.droelf.codereview.ui.fragment

import android.support.v4.app.FragmentManager
import at.droelf.codereview.dagger.activity.MainActivityComponent
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.Model
import at.droelf.codereview.network.GithubAuthService
import at.droelf.codereview.provider.GithubAuthProvider
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.utils.FirebaseHelper
import at.droelf.codereview.utils.RxHelper
import com.firebase.client.AuthData
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class LoginFragmentController(val mainActivityController: MainActivityController,
                              val githubAuthProvider: GithubAuthProvider): RxHelper, FirebaseHelper {

    fun initLogin(mainActivityComponent: MainActivityComponent, fm: FragmentManager, token: String){
        mainActivityController.createUserComponent(mainActivityComponent, Model.GithubAuth(token))
        mainActivityController.displayNotificationFragment(fm)
    }

    fun getToken(username: String, password: String, totpToken: String? = null)
            : Observable<Pair<GithubModel.AuthReturnType, GithubModel.AuthResponse?>> {

        return githubAuthProvider.authToken(username, password, totpToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    }

}