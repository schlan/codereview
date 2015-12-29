package at.droelf.codereview.ui.fragment

import android.support.v4.app.FragmentManager
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.utils.RxHelper
import rx.Observable


class NotificationFragmentController(val mainActivityController: MainActivityController, val githubService: GithubService): RxHelper {

    var observable: Observable<List<GithubModel.Notification>>? = null


    fun loadNotifications(): Observable<List<GithubModel.Notification>> {

        if(observable == null){
            observable = githubService.notificationsRx()
                    .compose(transformObservable<List<GithubModel.Notification>>())
                    .map{ it.filter { n -> n.subject.type.equals("PullRequest") } }
                    .cache()
        }

        return observable!!
    }

    fun displayFileFragment(fm: FragmentManager, owner: String, repo: String, id: Long){
        mainActivityController.displayFilesFragment(fm, owner, repo, id)
    }

}