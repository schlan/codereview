package at.droelf.codereview.dagger.activity

import at.droelf.codereview.ui.activity.MainActivity
import at.droelf.codereview.ui.activity.MainActivityController
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule(private val activity: MainActivity) {

    @ActivityScope
    @Provides
    fun provideMainActivity(): MainActivity{
        return activity
    }

    @ActivityScope
    @Provides
    fun provideMainActivityController(mainActivity: MainActivity): MainActivityController{
        return MainActivityController(mainActivity)
    }

}


