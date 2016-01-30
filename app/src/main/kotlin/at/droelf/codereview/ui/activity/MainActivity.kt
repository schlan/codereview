package at.droelf.codereview.ui.activity

import android.os.Bundle
import android.widget.FrameLayout
import at.droelf.codereview.Global
import at.droelf.codereview.R
import at.droelf.codereview.dagger.activity.MainActivityComponent
import at.droelf.codereview.dagger.activity.MainActivityModule
import butterknife.Bind
import javax.inject.Inject

class MainActivity : BaseActivity<MainActivityComponent>(){

    var controller: MainActivityController? = null
        @Inject set

    //@Inject lateinit var controller: MainActivityController

    @Bind(R.id.main_container) lateinit var mainContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if(controller!!.accountInstalled()){
            if(savedInstanceState == null) {
                println("Install fragment")
                controller!!.displayNotificationFragment(supportFragmentManager)
            }
        } else {
            controller!!.displayLoginFragment(supportFragmentManager)
        }
    }

    override fun injectComponent(component: MainActivityComponent) {
        if(controller == null) {
            component.inject(this)
        }
    }

    override fun createComponent(): MainActivityComponent {
        return Global.get(this).appComponent.plus(MainActivityModule())
    }
}