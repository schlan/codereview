package at.droelf.codereview.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import at.droelf.codereview.dagger.activity.MainActivityComponent
import at.droelf.codereview.ui.activity.MainActivity
import java.util.logging.Logger

abstract class BaseFragment<E> : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectComponent(createComponent((context as MainActivity)))
        println("${this.javaClass} onCreate")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        retainInstance = true
    }

    abstract fun injectComponent(component: E): Unit
    abstract fun createComponent(mainActivity: MainActivity): E
}