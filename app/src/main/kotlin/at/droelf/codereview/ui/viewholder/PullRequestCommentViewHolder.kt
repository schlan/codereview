package at.droelf.codereview.ui.viewholder

import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.fragment.StartFragmentController
import at.droelf.codereview.ui.view.HtmlTextViewMagic
import at.droelf.codereview.utils.CircleTransform
import butterknife.Bind
import butterknife.ButterKnife
import com.squareup.picasso.Picasso
import org.sufficientlysecure.htmltextview.HtmlTextView

class PullRequestCommentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    val avatar: ImageView
    val userName: TextView
    val userComment: HtmlTextView
    val showCommentButton: Button

    init {
        ButterKnife.bind(this, view)
        avatar = view.findViewById(R.id.row_pr_comment_avatar) as ImageView
        userName = view.findViewById(R.id.row_pr_name) as TextView
        userComment = view.findViewById(R.id.row_pr_comment) as HtmlTextView
        showCommentButton = view.findViewById(R.id.row_pr_comment_button) as Button
    }

    fun bind(comment: GithubModel.Comment, controller: StartFragmentController) {
        userComment.text = ""

        userName.text = "@${comment.user.login}"
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