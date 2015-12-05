package at.droelf.codereview.dagger.activity

import at.droelf.codereview.MainActivity
import at.droelf.codereview.dagger.activity.ActivityScope
import dagger.Module
import dagger.Provides
import dagger.Subcomponent


@Module
class MainActivityModule(private val mainActivity: MainActivity) {

    @Provides
    @ActivityScope
    fun provideMainActivity(): MainActivity {
        return mainActivity
    }

}

