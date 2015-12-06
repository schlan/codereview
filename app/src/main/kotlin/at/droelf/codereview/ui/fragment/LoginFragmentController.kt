package at.droelf.codereview.ui.fragment

import at.droelf.codereview.model.Model
import at.droelf.codereview.ui.activity.MainActivityController


class LoginFragmentController(val mainActivityController: MainActivityController, val loginFragment: LoginFragment) {

    fun initLogin(token: String){
        mainActivityController.createUserComponent(Model.GithubAuth(token))
        mainActivityController.displayFilesFragment()
    }

}