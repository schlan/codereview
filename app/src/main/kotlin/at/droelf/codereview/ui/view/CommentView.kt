package at.droelf.codereview.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.model.Model
import at.droelf.codereview.utils.CircleTransform
import at.droelf.codereview.utils.UiHelper
import com.squareup.picasso.Picasso


class CommentView(val comment: Model.ReviewComment, context: Context) : FrameLayout(context), UiHelper {

    lateinit var commentView: TextView
    lateinit var commentTitle: TextView
    lateinit var avatar: ImageView

    lateinit var container: LinearLayout
    lateinit var cardView: FrameLayout

    lateinit var dividerBottom: View

    lateinit var timeStamp: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.row_patchadapter_comment_single, this, true)
        commentView = findViewById(R.id.row_patch_comment) as TextView
        commentTitle = findViewById(R.id.row_patch_name) as TextView
        avatar = findViewById(R.id.row_patch_comment_avatar) as ImageView
        container = findViewById(R.id.row_patch_comment_card_container) as LinearLayout
        cardView = findViewById(R.id.row_pr_comment_container) as FrameLayout
        dividerBottom = findViewById(R.id.row_patch_comment_line_divider_bottom)
        timeStamp = findViewById(R.id.row_patch_timestamp) as TextView

        commentTitle.text = comment.user.login
        timeStamp(timeStamp, comment.createdAt)
        dividerBottom.visibility = View.GONE


        HtmlTextViewMagic.apply(commentView, comment.bodyHtml)
        Picasso.with(context)
                .load(comment.user.avatarUrl)
                .transform(CircleTransform())
                .into(avatar)
    }

    fun first(){
        //val params = (cardView.layoutParams as FrameLayout.LayoutParams)
        //params.topMargin = context.resources.getDimensionPixelSize(R.dimen.row_patchadapter_comment_padding_top)
    }

    fun last(){
        //val params = (cardView.layoutParams as FrameLayout.LayoutParams)
        //params.bottomMargin = context.resources.getDimensionPixelSize(R.dimen.row_patchadapter_comment_padding_bottom)
        dividerBottom.visibility = View.VISIBLE
    }
}
