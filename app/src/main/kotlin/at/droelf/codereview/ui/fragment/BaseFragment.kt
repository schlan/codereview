package at.droelf.codereview.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import at.droelf.codereview.ui.activity.MainActivity
import timber.log.Timber.d

abstract class BaseFragment<E> : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        d("${this.javaClass} onCreate")

        val component = createComponent((context as MainActivity))
        if(component != null){
            injectComponent(component)
            d("${this.javaClass} injection done")
        } else {
            d("${this.javaClass} injection failed - null")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        retainInstance = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    abstract fun injectComponent(component: E): Unit
    abstract fun createComponent(mainActivity: MainActivity): E?
}