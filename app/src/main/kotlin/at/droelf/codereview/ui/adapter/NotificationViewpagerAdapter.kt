package at.droelf.codereview.ui.adapter;

import android.support.v4.app.FragmentManager
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import at.droelf.codereview.ui.fragment.NotificationFragmentController
import at.droelf.codereview.ui.view.NotificationPullRequestView
import java.lang.ref.WeakReference

class NotificationViewpagerAdapter(val fm: WeakReference<FragmentManager>, val controller: NotificationFragmentController) : PagerAdapter(), UnsubscribeRx {

    var prView: NotificationPullRequestView? = null
    var issueView: FrameLayout? = null

    override fun isViewFromObject(view: View?, o: Any?): Boolean {
        return view == o
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any? {
        val view = when(position){
            0 -> {
                if(prView == null){
                    prView = NotificationPullRequestView(container.context, fm, controller)
                }
                prView!!
            }
            1 -> {
                if(issueView == null){
                    issueView = FrameLayout(container.context)
                }
                return issueView!!
            }
            else -> throw IllegalArgumentException("Unknown pos: $position")
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, o: Any) {
        container.removeView(o as FrameLayout)
    }

    override fun getCount(): Int {
        return 2
    }

    override fun unsubscribeRx() {
        prView?.unsubscribeRx()
    }

}