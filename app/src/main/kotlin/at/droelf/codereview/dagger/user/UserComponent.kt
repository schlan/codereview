package at.droelf.codereview.dagger.user

import at.droelf.codereview.dagger.activity.PatchActivityComponent
import at.droelf.codereview.dagger.activity.PatchActivityModule
import at.droelf.codereview.dagger.activity.StartActivityComponent
import at.droelf.codereview.dagger.activity.StartActivityModule
import dagger.Subcomponent

@UserScope
@Subcomponent(
        modules = arrayOf(
                UserModule::class
        )
)
interface UserComponent {
    fun plus(patchActivityModule: PatchActivityModule): PatchActivityComponent
    fun plus(startActivityModule: StartActivityModule): StartActivityComponent
}