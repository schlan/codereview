package at.droelf.codereview.ui.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import at.droelf.codereview.R
import at.droelf.codereview.dagger.activity.MainActivityComponent
import at.droelf.codereview.dagger.user.UserComponent
import at.droelf.codereview.dagger.user.UserModule
import at.droelf.codereview.model.Model
import at.droelf.codereview.ui.fragment.LoginFragment
import at.droelf.codereview.ui.fragment.PatchFragment
import at.droelf.codereview.ui.fragment.StartFragment

class MainActivityController(val mainActivity: MainActivity) {

    private var userComponent: UserComponent? = null

    fun createUserComponent(data: Model.GithubAuth): UserComponent {
        userComponent = mainActivity.fragment.data!!.plus(UserModule(data))
        return userComponent ?: throw RuntimeException("it's dead jim")
    }

    fun releaseUserComponent() {
        userComponent = null
    }

    fun accountInstalled(): Boolean {
        return userComponent != null
    }

    fun userComponent(): UserComponent {
        return userComponent ?: throw RuntimeException("it's dead jim 2")
    }


    fun showFileFragment(contentsUrl: String?, patch: String?, filename: String?) {
        displayFileDiffFragment(contentsUrl, patch, filename)
    }

    fun displayLoginFragment(){
        fragmentTransaction { LoginFragment() }
    }

    fun displayFilesFragment(){
        fragmentTransaction { StartFragment() }
    }

    fun displayFileDiffFragment(contentsUrl: String?, patch: String?, filename: String?){
        fragmentTransaction {
            val fragment = PatchFragment()
            val bundle = Bundle()
            bundle.putString("url", contentsUrl)
            bundle.putString("patch", patch)
            bundle.putString("fname", filename)
            fragment.arguments = bundle
            fragment
        }
    }

    fun fragmentTransaction(fragment: () -> Fragment) {
        mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment())
                .commit()
    }
}