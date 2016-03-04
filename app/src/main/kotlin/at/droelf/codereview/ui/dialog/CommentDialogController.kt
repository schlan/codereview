package at.droelf.codereview.ui.dialog

import at.droelf.codereview.provider.GithubProvider
import at.droelf.codereview.ui.activity.MainActivityController
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class CommentDialogController(mainActivityController: MainActivityController, val githubProvider: GithubProvider) {

    var emojiObserver: Observable<Map<String, String>>? = null

    fun emojis(): Observable<Map<String, String>>{
        if(emojiObserver == null){
            emojiObserver = githubProvider.emoji()
                    .map { it.data }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
        }

        return emojiObserver!!
    }

}