package at.droelf.codereview.ui.fragment

import android.content.Context
import android.support.v4.app.Fragment
import at.droelf.codereview.dagger.activity.MainActivityComponent
import at.droelf.codereview.ui.activity.MainActivity

abstract class BaseFragment<E> : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        retainInstance = true
        injectComponent(createComponent((context as MainActivity)))
    }

    abstract fun injectComponent(component: E): Unit
    abstract fun createComponent(mainActivity: MainActivity): E
}