package at.droelf.codereview.dagger.fragment;

import at.droelf.codereview.dagger.activity.FragmentScope;
import at.droelf.codereview.ui.fragment.RepositoryFragment;
import dagger.Subcomponent;

@FragmentScope
@Subcomponent(
        modules = arrayOf(
                RepositoryFragmentModule::class
        )
)
interface RepositoryFragmentComponent {
    fun inject(repositoryFragment: RepositoryFragment): RepositoryFragment
}