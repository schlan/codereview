package at.droelf.codereview.dagger.fragment

import at.droelf.codereview.network.GithubService
import at.droelf.codereview.provider.GithubProvider
import at.droelf.codereview.ui.activity.MainActivityController
import at.droelf.codereview.ui.fragment.PatchFragment
import at.droelf.codereview.ui.fragment.PatchFragmentController
import dagger.Module
import dagger.Provides


@Module
class PatchFragmentModule(private val patchFragment: PatchFragment) {

    @Provides
    @FragmentScope
    fun providePatchFragment(): PatchFragment {
        return patchFragment
    }

    @Provides
    @FragmentScope
    fun providePatchFragmentController(mainActivityController: MainActivityController, githubProvider: GithubProvider): PatchFragmentController {
        return PatchFragmentController(mainActivityController, githubProvider)
    }

}

