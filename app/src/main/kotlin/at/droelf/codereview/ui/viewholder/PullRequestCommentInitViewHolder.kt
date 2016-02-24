package at.droelf.codereview.ui.viewholder

import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.view.HtmlTextViewMagic
import at.droelf.codereview.utils.UiHelper
import org.sufficientlysecure.htmltextview.HtmlTextView

class PullRequestCommentInitViewHolder(val view: View) : RecyclerView.ViewHolder(view), UiHelper {

    val timeStamp: TextView = view.findViewById(R.id.row_pr_date) as TextView
    val comment: HtmlTextView = view.findViewById(R.id.row_pr_comment) as HtmlTextView
    val buildIndicator: View = view.findViewById(R.id.row_pr_build_indicator)


    fun bind(pr: GithubModel.PullRequestDetail, status: GithubModel.Status?) {
        comment.text = ""
        buildIndicator.visibility = View.INVISIBLE

        timeStamp(timeStamp, pr.createdAt)
        HtmlTextViewMagic.apply(comment, pr.bodyHtml)

        buildStatus(status, pr.mergeable.toBoolean())
    }

    fun buildStatus(status: GithubModel.Status?, mergeable: Boolean){
        buildIndicator.visibility = View.VISIBLE

        val color = if(status != null){
            colorForBuildStatus(status.state)
        } else if(mergeable) {
            R.color.build_pass
        } else {
            R.color.build_fail
        }

        buildIndicator.background = ColorDrawable(ContextCompat.getColor(view.context, color))
    }
}