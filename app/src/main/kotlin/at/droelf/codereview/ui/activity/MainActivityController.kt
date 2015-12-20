package at.droelf.codereview.ui.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import at.droelf.codereview.R
import at.droelf.codereview.dagger.activity.MainActivityComponent
import at.droelf.codereview.dagger.user.UserComponent
import at.droelf.codereview.dagger.user.UserModule
import at.droelf.codereview.model.Model
import at.droelf.codereview.ui.fragment.LoginFragment
import at.droelf.codereview.ui.fragment.PatchFragment
import at.droelf.codereview.ui.fragment.StartFragment

class MainActivityController {

    private var userComponent: UserComponent? = null

    fun createUserComponent(appComponent: MainActivityComponent, data: Model.GithubAuth): UserComponent {
        userComponent = appComponent.plus(UserModule(data))
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

    fun showFileFragment(fm: FragmentManager, contentsUrl: String?, patch: String?, filename: String?) {
        displayFileDiffFragment(fm, contentsUrl, patch, filename)
    }

    fun displayLoginFragment(fm: FragmentManager){
        fragmentTransaction(fm, false) { LoginFragment() }
    }

    fun displayFilesFragment(fm: FragmentManager){
        fragmentTransaction(fm, false) { StartFragment() }
    }

    fun displayFileDiffFragment(fm: FragmentManager, contentsUrl: String?, patch: String?, filename: String?){
        fragmentTransaction(fm, true) {
            val fragment = PatchFragment()
            val bundle = Bundle()
            bundle.putString("url", contentsUrl)
            bundle.putString("patch", patch)
            bundle.putString("fname", filename)
            fragment.arguments = bundle
            fragment
        }
    }

    fun fragmentTransaction(fragmentManager: FragmentManager, backstack: Boolean, fragment: () -> Fragment) {
        val transaction = fragmentManager.beginTransaction()

        if(backstack) {
            transaction.addToBackStack("fooBar")
        }

        transaction.replace(R.id.main_container, fragment())
                .commit()
    }
}