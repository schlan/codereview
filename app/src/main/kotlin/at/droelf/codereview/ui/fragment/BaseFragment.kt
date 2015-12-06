package at.droelf.codereview.ui.fragment

import android.content.Context
import android.support.v4.app.Fragment

abstract class BaseFragment<E> : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        retainInstance = true
        injectComponent(createComponent(context))
    }


    abstract fun injectComponent(component: E): Unit
    abstract fun createComponent(context: Context): E
}