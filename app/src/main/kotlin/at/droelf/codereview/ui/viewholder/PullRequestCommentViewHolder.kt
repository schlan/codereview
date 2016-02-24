package at.droelf.codereview.ui.viewholder

import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
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
import at.droelf.codereview.utils.HumanTime
import com.squareup.picasso.Picasso
import org.sufficientlysecure.htmltextview.HtmlTextView
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class PullRequestCommentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    val avatar: ImageView = view.findViewById(R.id.row_pr_comment_avatar) as ImageView

    val headerContainer: View = view.findViewById(R.id.row_pr_header_holder)
    val userName: TextView = view.findViewById(R.id.row_pr_name) as TextView
    val timeStamp: TextView = view.findViewById(R.id.row_pr_date) as TextView

    val userComment: HtmlTextView = view.findViewById(R.id.row_pr_comment) as HtmlTextView
    val showCommentButton: Button = view.findViewById(R.id.row_pr_comment_button) as Button

    val indicator: View = view.findViewById(R.id.row_pr_build_indicator)

    fun bind(comment: GithubModel.Comment, controller: StartFragmentController) {
        userComment.text = ""
        indicator.visibility = View.INVISIBLE

        userName.text = "@${comment.user.login}"
        initTimeStamp(timeStamp, comment.createdAt)
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

    private fun initTimeStamp(textView: TextView, dateN: Date?) {
        val date = dateN ?: return
        val timeSpan = System.currentTimeMillis() - date.time
        if (timeSpan < TimeUnit.DAYS.toMillis(7)) {
            textView.text = HumanTime.approximately(System.currentTimeMillis() - date.time)
        } else {
            textView.text = SimpleDateFormat("dd MMM yyyy").format(date)
        }
    }

    fun showAvatar(show: Boolean) {
        val showFlag = if(show) View.VISIBLE else View.GONE
        avatar.visibility = showFlag
        headerContainer.visibility = showFlag
    }

    fun showBuildStatus(status: GithubModel.Status){
        indicator.visibility = View.VISIBLE
        val color = when (status.state) {
            "pending" -> R.color.build_pending
            "failure" -> R.color.build_fail
            "success" -> R.color.build_pass
            else -> R.color.build_pending
        }
        indicator.background = ColorDrawable(ContextCompat.getColor(view.context, color))
    }
}