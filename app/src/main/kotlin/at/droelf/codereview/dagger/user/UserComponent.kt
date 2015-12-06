package at.droelf.codereview.dagger.user

import at.droelf.codereview.dagger.fragment.PatchFragmentComponent
import at.droelf.codereview.dagger.fragment.PatchFragmentModule
import at.droelf.codereview.dagger.fragment.StartFragmentComponent
import at.droelf.codereview.dagger.fragment.StartFragmentModule
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
}