package at.droelf.codereview.dagger.activity

import at.droelf.codereview.MainActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent(
    modules = arrayOf(
            MainActivityModule::class
    )
)
interface MainActivityComponent {
    fun inject(mainActivity: MainActivity): MainActivity
}