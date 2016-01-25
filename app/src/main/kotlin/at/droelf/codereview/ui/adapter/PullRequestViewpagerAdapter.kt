package at.droelf.codereview.ui.adapter

import android.support.v4.app.FragmentManager
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.fragment.StartFragmentController
import at.droelf.codereview.ui.view.PullRequestCommentView

class PullRequestViewpagerAdapter(val fm: FragmentManager, val controller: StartFragmentController, val pr: GithubModel.PullRequestDetail): PagerAdapter() {

    override fun isViewFromObject(view: View?, o: Any?): Boolean {
        return view == o
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any? {
        val view = when(position){
            0 -> PullRequestCommentView(container.context, pr, fm, controller)
            1 -> FrameLayout(container.context)
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

}