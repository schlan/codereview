package at.droelf.codereview.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.view.HtmlTextViewMagic
import at.droelf.codereview.utils.CircleTransform
import butterknife.Bind
import butterknife.ButterKnife
import com.squareup.picasso.Picasso
import org.sufficientlysecure.htmltextview.HtmlTextView

class PullRequestCommentViewHolder(val view: View): RecyclerView.ViewHolder(view) {

    @Bind(R.id.row_pr_comment_avatar) lateinit var avatar: ImageView
    @Bind(R.id.row_pr_name) lateinit var userName: TextView
    @Bind(R.id.row_pr_comment) lateinit var userComment: HtmlTextView

    init {
        ButterKnife.bind(this, view)
        avatar = view.findViewById(R.id.row_pr_comment_avatar) as ImageView
        userName = view.findViewById(R.id.row_pr_name) as TextView
        userComment = view.findViewById(R.id.row_pr_comment) as HtmlTextView
    }

    fun bind(comment: GithubModel.Comment){
        userName.text = "@${comment.user.login}"
        HtmlTextViewMagic.apply(userComment, comment.bodyHtml)
        Picasso.with(view.context)
                .load(comment.user.avatarUrl)
                .transform(CircleTransform())
                .into(avatar)
    }
}