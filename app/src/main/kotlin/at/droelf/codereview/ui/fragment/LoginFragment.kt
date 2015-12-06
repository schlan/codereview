package at.droelf.codereview.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.droelf.codereview.Global
import at.droelf.codereview.R
import at.droelf.codereview.dagger.fragment.LoginFragmentComponent
import at.droelf.codereview.dagger.fragment.LoginFragmentModule
import at.droelf.codereview.model.Model
import at.droelf.codereview.ui.activity.MainActivity
import butterknife.ButterKnife
import javax.inject.Inject


class LoginFragment : BaseFragment<LoginFragmentComponent>() {

    @Inject lateinit var controller: LoginFragmentController
    @butterknife.Bind(R.id.login_api_token) lateinit var tokenView: android.widget.EditText
    @butterknife.Bind(R.id.login_submitbutton) lateinit var submitButton: android.widget.Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        submitButton.setOnClickListener(View.OnClickListener { view ->
        })
    }

    override fun injectComponent(component: LoginFragmentComponent) {
        component.inject(this)
    }

    override fun createComponent(context: Context): LoginFragmentComponent {
        return (context as MainActivity).fragment.data!!.plus(LoginFragmentModule(this))
    }

}