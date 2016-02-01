package at.droelf.codereview.dagger.fragment

import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.provider.GithubProvider
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.ui.fragment.NotificationFragment
import at.droelf.codereview.ui.fragment.NotificationFragmentController
import dagger.Module
import dagger.Provides


@Module
class NotificationFragmentModule(private val notificationFragment: NotificationFragment) {

    @Provides
    @FragmentScope
    fun providesNotificationFragment(): NotificationFragment {
        return notificationFragment
    }

    @Provides
    @FragmentScope
    fun providesNotificationFragmentController(mainActivityController: MainActivityController, githubProvider: GithubProvider, user: GithubModel.User): NotificationFragmentController {
        return NotificationFragmentController(mainActivityController, githubProvider, user)
    }

}