package at.droelf.codereview.ui.adapter

import android.support.v4.app.FragmentManager
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.fragment.StartFragmentController
import at.droelf.codereview.ui.view.PullRequestCommentView
import at.droelf.codereview.ui.view.PullRequestFileView

class PullRequestViewpagerAdapter(
        val fm: FragmentManager,
        val controller: StartFragmentController,
        val pr: GithubModel.PullRequestDetail,
        val status: GithubModel.Status?): PagerAdapter(), UnsubscribeRx {


    var commentView : PullRequestCommentView? = null
    var fileView : PullRequestFileView? = null

    override fun isViewFromObject(view: View?, o: Any?): Boolean {
        return view == o
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any? {
        val view: View = when(position){
            0 -> {
                if(commentView == null){
                    commentView = PullRequestCommentView(container.context, pr, fm, controller, status)
                }
                commentView!!
            }
            1 -> {
                if(fileView == null){
                    fileView = PullRequestFileView(container.context, pr, fm, controller)
                }
                fileView!!
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
        commentView?.unsubscribeRx()
        fileView?.unsubscribeRx()
    }
}