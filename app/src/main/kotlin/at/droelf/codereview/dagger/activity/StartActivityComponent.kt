package at.droelf.codereview.dagger.activity

import at.droelf.codereview.StartActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent(
        modules = arrayOf(
                StartActivityModule::class
        )
)

interface StartActivityComponent {
    fun inject(startActivity: StartActivity): StartActivity
}