package at.droelf.codereview.dagger.fragment

import at.droelf.codereview.dagger.activity.FragmentScope
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.ui.activity.MainActivity
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
    fun providePatchFragmentController(mainActivity: MainActivity, githubService: GithubService): PatchFragmentController {
        return PatchFragmentController(mainActivity, githubService)
    }

}

