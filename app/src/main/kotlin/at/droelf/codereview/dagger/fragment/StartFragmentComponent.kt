package at.droelf.codereview.dagger.fragment

import at.droelf.codereview.ui.fragment.StartFragment
import dagger.Subcomponent

@FragmentScope
@Subcomponent(
        modules = arrayOf(
                StartFragmentModule::class
        )
)

interface StartFragmentComponent {
    fun inject(startFragment: StartFragment): StartFragment
}