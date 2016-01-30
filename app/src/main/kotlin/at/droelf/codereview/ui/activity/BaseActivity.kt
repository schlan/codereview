package at.droelf.codereview.ui.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


abstract class BaseActivity<E> : AppCompatActivity() {

    private val tag: String = "headless_fragment"
    lateinit var fragment: HeadlessFragment<E>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("    onCreate Base")
        fragment = getHeadlessFragment()
        if(fragment.data == null){
            fragment.data = createComponent()
        }

        println("    inject Base")
        injectComponent(fragment.data!!)
    }

    override fun onStart() {
        super.onStart()
        injectComponent(fragment.data!!)
    }

    fun mainComponent(): E {
        return fragment.data!!
    }

    abstract fun injectComponent(component: E): Unit
    abstract fun createComponent(): E

    private fun installHeadlessFragment(): HeadlessFragment<E> {
        val fragment = HeadlessFragment<E>()
        supportFragmentManager.beginTransaction().add(fragment, tag).commit()
        return fragment
    }

    private fun getHeadlessFragment(): HeadlessFragment<E> {
        if(supportFragmentManager.findFragmentByTag(tag) is HeadlessFragment<*>){
            return supportFragmentManager.findFragmentByTag(tag) as HeadlessFragment<E>
        } else {
            return installHeadlessFragment()
        }
    }

    class HeadlessFragment<E> : Fragment() {
        var data: E? = null

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            retainInstance = true
            return null
        }
    }
}