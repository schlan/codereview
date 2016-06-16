package at.droelf.codereview.ui.viewholder

import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.fragment.StartFragmentController
import at.droelf.codereview.ui.view.HtmlTextViewMagic
import at.droelf.codereview.utils.UiHelper
import com.squareup.picasso.Picasso
import org.sufficientlysecure.htmltextview.HtmlTextView

class PullRequestCommentInitViewHolder(val view: View) : RecyclerView.ViewHolder(view), UiHelper {

    val timeStamp: TextView = view.findViewById(R.id.row_pr_date) as TextView
    val comment: HtmlTextView = view.findViewById(R.id.row_pr_comment) as HtmlTextView
    val buildIndicator: View = view.findViewById(R.id.row_pr_build_indicator)

    val reactionLaugh: ImageView = view.findViewById(R.id.row_pr_comment_reaction_laugh) as ImageView
    val reactionHeart: ImageView = view.findViewById(R.id.row_pr_comment_reaction_heart) as ImageView
    val reactionPlus: ImageView = view.findViewById(R.id.row_pr_comment_reaction_plus) as ImageView
    val reactionMinus: ImageView = view.findViewById(R.id.row_pr_comment_reaction_minus) as ImageView
    val reactionConfused: ImageView = view.findViewById(R.id.row_pr_comment_reaction_confused) as ImageView
    val reactionHooray: ImageView = view.findViewById(R.id.row_pr_comment_reaction_hooray) as ImageView

    val reactionMap: Map<GithubModel.ReactionType, ImageView> = mapOf(
            GithubModel.ReactionType.Confused to reactionConfused,
            GithubModel.ReactionType.Heart to reactionHeart,
            GithubModel.ReactionType.Hooray to reactionHooray,
            GithubModel.ReactionType.Laugh to reactionLaugh,
            GithubModel.ReactionType.Plus to reactionPlus,
            GithubModel.ReactionType.Minus to reactionMinus
    )

    fun bind(data: StartFragmentController.PullRequestDetails) {
        comment.text = ""
        buildIndicator.visibility = View.INVISIBLE

        timeStamp(timeStamp, data.githubPrDetails.createdAt)
        HtmlTextViewMagic.apply(comment, data.githubPrDetails.bodyHtml)

        buildStatus(data.status, data.githubPrDetails.mergeable.toBoolean())
        reactions(data.reactions)
    }

    fun reactions(reaction: Map<GithubModel.ReactionItem, Int>){
        reaction.keys.forEach { reaction ->
            val imageView = reactionMap[reaction.type]
            imageView?.visibility = View.VISIBLE
            Picasso
                    .with(view.context)
                    .load(reaction.url)
                    .into(imageView)
        }
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