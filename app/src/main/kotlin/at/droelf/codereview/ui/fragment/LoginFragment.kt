package at.droelf.codereview.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import at.droelf.codereview.R
import at.droelf.codereview.dagger.fragment.LoginFragmentComponent
import at.droelf.codereview.dagger.fragment.LoginFragmentModule
import at.droelf.codereview.ui.activity.MainActivity
import javax.inject.Inject
import kotlinx.android.synthetic.fragment_login.*


class LoginFragment : BaseFragment<LoginFragmentComponent>() {

    @Inject lateinit var controller: LoginFragmentController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        login_submitbutton.setOnClickListener({ view ->
            controller.initLogin((activity as MainActivity).mainComponent(), fragmentManager, login_api_token.text.toString())
        })
    }

    override fun injectComponent(component: LoginFragmentComponent) {
        component.inject(this)
    }

    override fun createComponent(mainActivity: MainActivity): LoginFragmentComponent {
        return mainActivity.mainComponent().plus(LoginFragmentModule(this))
    }

}