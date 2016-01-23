package at.droelf.codereview.dagger.fragment

import at.droelf.codereview.ui.fragment.NotificationFragment
import dagger.Subcomponent

@FragmentScope
@Subcomponent(
        modules = arrayOf(
                NotificationFragmentModule::class
        )
)
interface NotificationFragmentComponent{
    fun inject(notificationFragment: NotificationFragment): NotificationFragment
}