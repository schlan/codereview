package at.droelf.codereview.ui.activity

import android.os.Bundle
import android.widget.FrameLayout
import at.droelf.codereview.Global
import at.droelf.codereview.R
import at.droelf.codereview.dagger.activity.MainActivityComponent
import at.droelf.codereview.dagger.activity.MainActivityModule
import timber.log.Timber
import timber.log.Timber.d
import javax.inject.Inject

class MainActivity : BaseActivity<MainActivityComponent>(){

    var controller: MainActivityController? = null
        @Inject set

    lateinit var mainContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        mainContainer = findViewById(R.id.main_container) as FrameLayout
        mainContainer.setBackgroundResource(R.color.bg_gray)

        if(controller!!.accountInstalled()){
            if(savedInstanceState == null) {
                d("Install fragment")
                controller!!.displayNotificationFragment(supportFragmentManager)
            }
        } else {
            val result = controller?.tryToLoadAccount(mainComponent())
            if(result != null && result) {
                controller!!.displayNotificationFragment(supportFragmentManager)
            }else {
                controller!!.displayLoginFragment(supportFragmentManager)
            }
        }
    }

    fun getOrInit(): MainActivityController {
        if(controller == null){
            init()
            controller?.tryToLoadAccount(mainComponent())
        }
        return controller!!
    }

    override fun injectComponent(component: MainActivityComponent) {
        component.inject(this)
    }

    override fun createComponent(): MainActivityComponent {
        return Global.get(this).appComponent.plus(MainActivityModule())
    }
}