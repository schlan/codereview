package at.droelf.codereview.ui.activity

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.webkit.WebSettings
import android.webkit.WebView
import at.droelf.codereview.R
import at.droelf.codereview.dagger.activity.MainActivityComponent
import at.droelf.codereview.dagger.user.UserComponent
import at.droelf.codereview.dagger.user.UserModule
import at.droelf.codereview.model.Model
import at.droelf.codereview.storage.GithubUserStorage
import at.droelf.codereview.ui.dialog.CommentDialog
import at.droelf.codereview.ui.fragment.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GithubAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivityController(val githubUserStorage: GithubUserStorage) {

    private var userComponent: UserComponent? = null

    fun createUserComponent(appComponent: MainActivityComponent, data: Model.GithubAuth): UserComponent {
        userComponent = appComponent.plus(UserModule(data))
        return userComponent ?: throw RuntimeException("it's dead jim")
    }

    fun accountInstalled(): Boolean {
        return userComponent != null
    }

    fun tryToLoadAccount(appComponent: MainActivityComponent): Boolean {
        if(githubUserStorage.userStored()){
            val user = githubUserStorage.getUserBlocking() ?: return false
            loginToFireBase(user.auth.token)
            createUserComponent(appComponent, user)
            return true
        }
        return false
    }

    fun userComponent(): UserComponent {
        return userComponent ?: throw RuntimeException("it's dead jim 2")
    }

    fun loginToFireBase(githubToken: String): Unit {
        FirebaseAuth
                .getInstance()
                .signInWithCredential(GithubAuthProvider.getCredential(githubToken))
                .addOnCompleteListener { task ->
                    println("FirebaseLogin fun: ${task.isSuccessful} ${task.exception.toString()}")

                    FirebaseDatabase
                            .getInstance()
                            .reference.child("user")
                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onCancelled(p0: DatabaseError?) {

                                }

                                override fun onDataChange(data: DataSnapshot?) {
                                    val foobar = data?.value
                                    println(foobar)
                                }

                            })

                }
    }

    fun showFileFragment(fm: FragmentManager, contentsUrl: String?, patch: String?,
                         filename: String?, owner: String, repo: String, pullRequest: Long,
                         commitId: String, path: String) {
        displayFileDiffFragment(fm, contentsUrl, patch, filename, owner, repo, pullRequest, commitId, path)
    }

    fun displayRepositoryFragment(fm: FragmentManager, backstack: Boolean = false){
        fragmentTransaction(fm, backstack) { RepositoryFragment() }
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

    fun displayFileDiffFragment(fm: FragmentManager,
                                contentsUrl: String?, patch: String?, filename: String?,
                                owner: String, repo: String, pullRequest: Long, commitId: String,
                                path: String){
        fragmentTransaction(fm, true) {
            val fragment = PatchFragment()
            val bundle = Bundle()
            bundle.putString("url", contentsUrl)
            bundle.putString("patch", patch)
            bundle.putString("fname", filename)
            bundle.putString("owner", owner)
            bundle.putString("repo", repo)
            bundle.putLong("pr", pullRequest)
            bundle.putString("commitid", commitId)
            bundle.putString("path", path)
            fragment.arguments = bundle
            fragment
        }
    }

    fun fragmentTransaction(fragmentManager: FragmentManager, backstack: Boolean, fragment: () -> Fragment) {
        val transaction = fragmentManager.beginTransaction()
        val f = fragment()

        if(backstack) {
            transaction.addToBackStack(f.javaClass.simpleName)
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
            //transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
        }

        transaction
                .replace(R.id.main_container, f)
                .commit()
    }

    fun showWebViewDialog(context: Context, title: String, body: String){
        val dialog: AlertDialog.Builder = AlertDialog.Builder(context)
        dialog.setTitle(title)

        val html = context.getString(R.string.github_webview_content).format(body)

        val wv: WebView = WebView(context)
        wv.settings.loadWithOverviewMode = true
        wv.settings.useWideViewPort = true
        wv.settings.javaScriptEnabled = true
        wv.settings.builtInZoomControls = true
        wv.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING

        wv.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null)

        dialog.setView(wv);
        dialog.setNegativeButton("Close", { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
        });
        dialog.show()
    }

    fun showCommentDialogReviewComment(fragment: Fragment, owner: String, repo: String, number: Long,
                                       commitId: String, path: String, position: Int ){
        CommentDialog.startReviewCommentLine(fragment, owner, repo, number, commitId, path, position)
    }

    fun showCommentDialogReviewCommentReply(fragment: Fragment, owner: String, repo: String, number: Long, commentId: Long){
        CommentDialog.startReviewCommentReply(fragment, owner, repo, number, commentId)
    }

}