package at.droelf.codereview.dagger.activity

import at.droelf.codereview.ui.PatchActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent(
    modules = arrayOf(
            PatchActivityModule::class
    )
)
interface PatchActivityComponent {
    fun inject(patchActivity: PatchActivity): PatchActivity
}