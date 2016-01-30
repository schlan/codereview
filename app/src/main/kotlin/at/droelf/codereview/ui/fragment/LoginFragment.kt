package at.droelf.codereview.ui.fragment

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import at.droelf.codereview.R
import at.droelf.codereview.dagger.fragment.LoginFragmentComponent
import at.droelf.codereview.dagger.fragment.LoginFragmentModule
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.activity.MainActivity
import butterknife.Bind
import butterknife.ButterKnife
import rx.Observable
import javax.inject.Inject

class LoginFragment : BaseFragment<LoginFragmentComponent>() {

    @Inject lateinit var controller: LoginFragmentController

    lateinit var loginSubmitButton: FloatingActionButton
    lateinit var loginApiToken: EditText

    lateinit var loginUsername: EditText
    lateinit var loginPassword: EditText
    lateinit var loginTwoFactor: List<EditText>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginSubmitButton = view?.findViewById(R.id.login_submitbutton) as FloatingActionButton
        loginPassword = view?.findViewById(R.id.login_password) as EditText
        loginUsername = view?.findViewById(R.id.login_username) as EditText

        loginTwoFactor = listOf(
                view?.findViewById(R.id.login_twofactor_1) as EditText,
                view?.findViewById(R.id.login_twofactor_2) as EditText,
                view?.findViewById(R.id.login_twofactor_3) as EditText,
                view?.findViewById(R.id.login_twofactor_4) as EditText,
                view?.findViewById(R.id.login_twofactor_5) as EditText,
                view?.findViewById(R.id.login_twofactor_6) as EditText
        )
        loginTwoFactor.forEachIndexed { i, editText ->
            val nextView = if(i+1 < loginTwoFactor.size) loginTwoFactor[i+1] else null
            val prevView = if(i-1 >= 0) loginTwoFactor[i-1] else null
            val listener = TwoFactorListener(editText, nextView, prevView)
            editText.setOnKeyListener(listener)
            editText.addTextChangedListener(listener)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginSubmitButton.setOnClickListener({ view ->

            //val token: String? = if (loginTwoFactor.text.toString().isNotEmpty()) loginTwoFactor.text.toString() else null
            val o = controller.getToken(loginUsername.text.toString(), loginPassword.text.toString(), null)

            o.subscribe({ data ->
                println("Success: $data")
            }, { error ->
                println("Error: $error")
            })

        })
    }

    override fun injectComponent(component: LoginFragmentComponent) {
        component.inject(this)
    }

    override fun createComponent(mainActivity: MainActivity): LoginFragmentComponent? {
        return mainActivity.mainComponent().plus(LoginFragmentModule())
    }

    class TwoFactorListener(val thisView: EditText, val nextView: EditText?, val prevView: EditText?): View.OnKeyListener, TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(count == 0){
                prevView?.requestFocus()
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }

        override fun onKey(v: View, keyCode: Int, event: KeyEvent?): Boolean {
            val txt = thisView.text.toString()
            if(txt.length == 1 && Character.isDigit(txt.first())){
                nextView?.requestFocus()
            }
            return false
        }
    }

}