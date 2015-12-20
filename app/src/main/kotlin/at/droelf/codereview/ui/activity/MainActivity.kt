package at.droelf.codereview.ui.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.FrameLayout
import at.droelf.codereview.Global
import at.droelf.codereview.R
import at.droelf.codereview.dagger.activity.MainActivityComponent
import at.droelf.codereview.dagger.activity.MainActivityModule
import at.droelf.codereview.dagger.user.UserComponent
import at.droelf.codereview.dagger.user.UserModule
import at.droelf.codereview.model.Model
import at.droelf.codereview.ui.fragment.LoginFragment
import at.droelf.codereview.ui.fragment.StartFragment
import butterknife.Bind
import butterknife.ButterKnife
import javax.inject.Inject

class MainActivity : BaseActivity<MainActivityComponent>(){

    @Inject lateinit var controller: MainActivityController
    @Bind(R.id.main_container) lateinit var mainContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if(controller.accountInstalled()){
            if(savedInstanceState == null) {
                println("Install fragment")
                controller.displayFilesFragment(supportFragmentManager)
            }
        } else {
            controller.displayLoginFragment(supportFragmentManager)
        }
    }

    override fun injectComponent(component: MainActivityComponent) {
        component.inject(this)
    }

    override fun createComponent(): MainActivityComponent {
        return Global.get(this).appComponent.plus(MainActivityModule())
    }
}