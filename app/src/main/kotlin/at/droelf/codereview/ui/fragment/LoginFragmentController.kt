package at.droelf.codereview.ui.fragment

import android.support.v4.app.FragmentManager
import at.droelf.codereview.dagger.activity.MainActivityComponent
import at.droelf.codereview.model.Model
import at.droelf.codereview.ui.activity.MainActivityController


class LoginFragmentController(val mainActivityController: MainActivityController, val loginFragment: LoginFragment) {

    fun initLogin(mainActivityComponent: MainActivityComponent, fm: FragmentManager, token: String){
        mainActivityController.createUserComponent(mainActivityComponent, Model.GithubAuth(token, "fHn-ryhUTPa_TlsA47jufg"))
        mainActivityController.displayNotificationFragment(fm)
    }

}