package at.droelf.codereview.dagger.fragment

import at.droelf.codereview.dagger.activity.FragmentScope
import at.droelf.codereview.network.GithubService
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
    fun providesNotificationFragmentController(mainActivityController: MainActivityController, githubService: GithubService): NotificationFragmentController {
        return NotificationFragmentController(mainActivityController, githubService)
    }

}