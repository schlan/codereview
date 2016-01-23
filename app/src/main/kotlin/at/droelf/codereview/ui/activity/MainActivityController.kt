package at.droelf.codereview.ui.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import at.droelf.codereview.R
import at.droelf.codereview.dagger.activity.MainActivityComponent
import at.droelf.codereview.dagger.user.UserComponent
import at.droelf.codereview.dagger.user.UserModule
import at.droelf.codereview.model.Model
import at.droelf.codereview.ui.fragment.*

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


    fun showFileFragment(fm: FragmentManager, contentsUrl: String?, patch: String?, filename: String?, owner: String, repo: String, pullRequest: Long) {
        displayFileDiffFragment(fm, contentsUrl, patch, filename, owner, repo, pullRequest)
    }

    fun displayRepositoryFragment(fm: FragmentManager){
        fragmentTransaction(fm, false) { RepositoryFragment() }
    }

    fun displayLoginFragment(fm: FragmentManager){
        fragmentTransaction(fm, false) { LoginFragment() }
    }

    fun displayFilesFragment(fm: FragmentManager, owner: String, repo: String, id: Long){
        fragmentTransaction(fm, true) {
            val fragment = StartFragment()
            val bundle = Bundle()
            bundle.putString("owner", owner)
            bundle.putString("repo", repo)
            bundle.putLong("id", id)
            fragment.arguments = bundle
            fragment
        }
    }

    fun displayNotificationFragment(fm: FragmentManager){
        fragmentTransaction(fm, false){
            NotificationFragment()
        }
    }

    fun displayFileDiffFragment(fm: FragmentManager, contentsUrl: String?, patch: String?, filename: String?, owner: String, repo: String, pullRequest: Long){
        fragmentTransaction(fm, true) {
            val fragment = PatchFragment()
            val bundle = Bundle()
            bundle.putString("url", contentsUrl)
            bundle.putString("patch", patch)
            bundle.putString("fname", filename)
            bundle.putString("owner", owner)
            bundle.putString("repo", repo)
            bundle.putLong("pr", pullRequest)
            fragment.arguments = bundle
            fragment
        }
    }

    fun fragmentTransaction(fragmentManager: FragmentManager, backstack: Boolean, fragment: () -> Fragment) {
        val transaction = fragmentManager.beginTransaction()
        val f = fragment()

        if(backstack) {
            transaction.addToBackStack(f.javaClass.simpleName)
        }

        transaction.replace(R.id.main_container, f)
                .commit()
    }
}