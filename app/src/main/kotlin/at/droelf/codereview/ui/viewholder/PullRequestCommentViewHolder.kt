package at.droelf.codereview.ui.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.fragment.StartFragmentController
import at.droelf.codereview.ui.view.HtmlTextViewMagic
import at.droelf.codereview.utils.CircleTransform
import at.droelf.codereview.utils.UiHelper
import com.squareup.picasso.Picasso
import org.sufficientlysecure.htmltextview.HtmlTextView

class PullRequestCommentViewHolder(val view: View) : RecyclerView.ViewHolder(view), UiHelper {

    val avatar: ImageView = view.findViewById(R.id.row_pr_comment_avatar) as ImageView

    val userName: TextView = view.findViewById(R.id.row_pr_name) as TextView
    val timeStamp: TextView = view.findViewById(R.id.row_pr_date) as TextView

    val userComment: HtmlTextView = view.findViewById(R.id.row_pr_comment) as HtmlTextView
    val showCommentButton: Button = view.findViewById(R.id.row_pr_comment_button) as Button

    fun bind(comment: GithubModel.Comment, controller: StartFragmentController) {
        userComment.text = ""

        userName.text = "@${comment.user.login}"
        timeStamp(timeStamp, comment.createdAt)

        Picasso.with(view.context)
                .load(comment.user.avatarUrl)
                .transform(CircleTransform())
                .into(avatar)

        if(comment.bodyHtml.contains("<table")){
            showCommentButton.setOnClickListener {
                if (comment.bodyHtml.contains("<table")) {
                    controller.showDialog(view.context, "@${comment.user.login}", comment.bodyHtml)
                }
            }
            showCommentButton.visibility = View.VISIBLE
            userComment.visibility = View.GONE
        } else{
            HtmlTextViewMagic.apply(userComment, comment.bodyHtml)
            showCommentButton.visibility = View.GONE
            userComment.visibility = View.VISIBLE
        }
    }
}