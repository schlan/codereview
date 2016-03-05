package at.droelf.codereview.ui.dialog

import at.droelf.codereview.model.Model
import at.droelf.codereview.provider.GithubProvider
import at.droelf.codereview.storage.GithubUserStorage
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

class CommentDialogController(val githubProvider: GithubProvider, val githubUserStorage: GithubUserStorage) {

    var emojiObserver: Observable<Map<String, String>>? = null
    var presetObserver: Observable<List<Model.CommentPreset>>? = null

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

    fun presets(): Observable<List<Model.CommentPreset>> {
        if(presetObserver == null){
            presetObserver = Observable.defer { Observable.just(githubUserStorage.getCommentPresets()) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
        return presetObserver!!
    }

    fun deletePreset(preset: Model.CommentPreset) {
        githubUserStorage.deletePreset(preset)
    }

    fun addPreset(comment: String) {
        githubUserStorage.addCommentPreset(comment)
    }

}