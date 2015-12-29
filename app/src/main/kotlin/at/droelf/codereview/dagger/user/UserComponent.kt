package at.droelf.codereview.dagger.user

import at.droelf.codereview.dagger.fragment.*
import dagger.Subcomponent

@UserScope
@Subcomponent(
        modules = arrayOf(
                UserModule::class
        )
)
interface UserComponent {
    fun plus(patchFragmentModule: PatchFragmentModule): PatchFragmentComponent
    fun plus(startFragmentModule: StartFragmentModule): StartFragmentComponent
    fun plus(repositoryFragmentModule: RepositoryFragmentModule): RepositoryFragmentComponent
    fun plus(notificationFragmentModule: NotificationFragmentModule): NotificationFragmentComponent
}