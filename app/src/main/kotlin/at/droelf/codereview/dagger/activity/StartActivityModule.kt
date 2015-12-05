package at.droelf.codereview.dagger.activity

import at.droelf.codereview.StartActivity
import dagger.Module
import dagger.Provides
import dagger.Subcomponent

@Module
class StartActivityModule(private val startActivity: StartActivity) {
    @Provides
    @ActivityScope
    fun provideStartActivity(): StartActivity {
        return startActivity
    }
}