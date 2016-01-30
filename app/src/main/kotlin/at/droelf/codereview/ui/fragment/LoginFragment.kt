package at.droelf.codereview.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import at.droelf.codereview.R
import at.droelf.codereview.dagger.fragment.LoginFragmentComponent
import at.droelf.codereview.dagger.fragment.LoginFragmentModule
import at.droelf.codereview.ui.activity.MainActivity
import butterknife.Bind
import butterknife.ButterKnife
import javax.inject.Inject

class LoginFragment : BaseFragment<LoginFragmentComponent>() {

    @Inject lateinit var controller: LoginFragmentController
    @Bind(R.id.login_submitbutton) lateinit var loginSubmitButton: Button
    @Bind(R.id.login_api_token) lateinit var loginApiToken: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginSubmitButton.setOnClickListener({ view ->
            controller.initLogin((activity as MainActivity).mainComponent(), fragmentManager, loginApiToken.text.toString())
        })
    }

    override fun injectComponent(component: LoginFragmentComponent) {
        component.inject(this)
    }

    override fun createComponent(mainActivity: MainActivity): LoginFragmentComponent? {
        return mainActivity.mainComponent().plus(LoginFragmentModule(this))
    }

}