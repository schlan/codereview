package at.droelf.codereview.dagger.fragment

import at.droelf.codereview.ui.fragment.PatchFragment
import dagger.Subcomponent

@FragmentScope
@Subcomponent(
    modules = arrayOf(
            PatchFragmentModule::class
    )
)
interface PatchFragmentComponent {
    fun inject(patchFragment: PatchFragment): PatchFragment
}