package at.droelf.codereview.dagger.fragment

import at.droelf.codereview.provider.GithubProvider
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.ui.dialog.CommentDialogController
import dagger.Module
import dagger.Provides

@Module
class CommentDialogModule() {

    @Provides
    @FragmentScope
    fun providesCommentDialogController(mainActivityController: MainActivityController, githubProvider: GithubProvider): CommentDialogController {
        return CommentDialogController(mainActivityController, githubProvider)
    }

}