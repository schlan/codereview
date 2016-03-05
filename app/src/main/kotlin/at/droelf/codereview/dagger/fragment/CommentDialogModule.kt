package at.droelf.codereview.dagger.fragment

import at.droelf.codereview.provider.GithubProvider
import at.droelf.codereview.storage.GithubUserStorage
import at.droelf.codereview.ui.dialog.CommentDialogController
import dagger.Module
import dagger.Provides

@Module
class CommentDialogModule() {

    @Provides
    @FragmentScope
    fun providesCommentDialogController(githubProvider: GithubProvider, githubUserStorage: GithubUserStorage): CommentDialogController {
        return CommentDialogController(githubProvider, githubUserStorage)
    }

}