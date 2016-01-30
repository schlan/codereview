package at.droelf.codereview.ui.fragment

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import at.droelf.codereview.R
import at.droelf.codereview.dagger.fragment.LoginFragmentComponent
import at.droelf.codereview.dagger.fragment.LoginFragmentModule
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.activity.MainActivity
import javax.inject.Inject

class LoginFragment : BaseFragment<LoginFragmentComponent>() {

    @Inject lateinit var controller: LoginFragmentController

    lateinit var loginSubmitButton: FloatingActionButton

    lateinit var loginUsername: EditText
    lateinit var loginPassword: EditText

    lateinit var twoFactorContainer: View
    lateinit var loginTwoFactor: List<EditText>
    lateinit var twoFactorPaste: ImageView

    lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginSubmitButton = view?.findViewById(R.id.login_submitbutton) as FloatingActionButton
        loginPassword = view?.findViewById(R.id.login_password) as EditText
        loginUsername = view?.findViewById(R.id.login_username) as EditText
        twoFactorPaste = view?.findViewById(R.id.login_twofactor_paste) as ImageView
        twoFactorContainer = view?.findViewById(R.id.login_twofactor_container) as View
        progressBar = view?.findViewById(R.id.login_progressbar) as ProgressBar
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

        twoFactorPaste.setOnClickListener({ view ->
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if(clipboard.hasPrimaryClip()){
                val clipContent = clipboard.primaryClip.getItemAt(0).text
                if(clipContent.length == 6 && clipContent.matches("[0-9]+".toRegex())){
                    loginTwoFactor.forEachIndexed { i, editText ->
                        editText.setText(clipContent[i].toString())
                    }
                }
            }
        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginSubmitButton.setOnClickListener({ view ->

            loginUsername.error = null
            loading(true)
            val o = if(twoFactorContainer.visibility == View.VISIBLE){
                val token = loginTwoFactor.fold("", {s, e -> "$s${e.text.toString()}"})
                controller.getToken(loginUsername.text.toString(), loginPassword.text.toString(), token)
            } else {
                controller.getToken(loginUsername.text.toString(), loginPassword.text.toString())
            }

            o.subscribe({ data ->
                loading(false)
                when(data.first) {
                    GithubModel.AuthReturnType.TwoFactorApp, GithubModel.AuthReturnType.TwoFactorUnknown, GithubModel.AuthReturnType.TwoFactorSms -> {
                        twoFactorContainer.visibility = View.VISIBLE
                        loginUsername.isEnabled = false
                        loginPassword.isEnabled = false
                        loginUsername.error = "TOTP token needed"
                    }
                    GithubModel.AuthReturnType.Error -> {
                        twoFactorContainer.visibility = View.GONE
                        loginUsername.error = "Login Error"
                    }
                    GithubModel.AuthReturnType.Success -> {
                        initApp(data.second!!)
                    }
                }

                println("Success: $data")
            }, { error ->
                loading(false)
                loginUsername.error = "Error"
                println("Error: $error")
            })
        })
    }

    fun initApp(second: GithubModel.AuthResponse) {
        loading(true)
        //FIXME init all the stuff
    }

    override fun injectComponent(component: LoginFragmentComponent) {
        component.inject(this)
    }

    override fun createComponent(mainActivity: MainActivity): LoginFragmentComponent? {
        return mainActivity.mainComponent().plus(LoginFragmentModule())
    }

    private fun loading(loading: Boolean) {
        progressBar.visibility = if(loading) View.VISIBLE else View.GONE
        val elements: List<View> = listOf(loginSubmitButton, loginUsername, loginPassword) + loginTwoFactor + twoFactorPaste
        elements.forEach { view ->
            view.isEnabled = !loading
        }
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